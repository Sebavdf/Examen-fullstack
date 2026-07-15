package com.fullrepar.bff.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;


@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    private final SecretKey signingKey;
    private final List<String> publicPaths;

    public JwtAuthFilter(@Value("${jwt.secret}") String secret,
                          @Value("${jwt.public-paths}") String publicPaths) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.publicPaths = Arrays.asList(publicPaths.split(","));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {

        if (isPublicPath(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Rejected request to {}: missing or malformed Authorization header", request.getRequestURI());
            writeForbidden(response, request.getRequestURI(), "Missing or malformed Authorization header. Expected: Bearer <token>");
            return;
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // Propagamos identidad hacia los microservicios downstream vía headers,
            // para que puedan usarla sin tener que re-parsear el JWT ellos mismos.
            String username = claims.getSubject();
            String role = claims.get("role", String.class);

            HeaderForwardingRequestWrapper wrappedRequest =
                    new HeaderForwardingRequestWrapper(request, username, role);

            filterChain.doFilter(wrappedRequest, response);

        } catch (ExpiredJwtException ex) {
            log.warn("Rejected request to {}: token expired", request.getRequestURI());
            writeForbidden(response, request.getRequestURI(), "Token expired");
        } catch (JwtException ex) {
            log.warn("Rejected request to {}: invalid token ({})", request.getRequestURI(), ex.getMessage());
            writeForbidden(response, request.getRequestURI(), "Invalid token");
        }
    }

    private boolean isPublicPath(String uri) {
        return publicPaths.stream().anyMatch(pattern -> PATH_MATCHER.match(pattern, uri));
    }

   
    private void writeForbidden(HttpServletResponse response, String path, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String json = """
                {
                  "timestamp": "%s",
                  "status": 403,
                  "error": "Forbidden",
                  "message": "%s",
                  "path": "%s"
                }
                """.formatted(LocalDateTime.now(), message, path);

        response.getWriter().write(json);
    }
}
