package com.fullrepar.auth_service.controller;

import com.fullrepar.auth_service.dto.LoginRequest;
import com.fullrepar.auth_service.dto.RegisterRequest;
import com.fullrepar.auth_service.dto.RegisterResponse;
import com.fullrepar.auth_service.dto.TokenResponse;
import com.fullrepar.auth_service.model.User;
import com.fullrepar.auth_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller handling HTTP requests for user authentication (Login and Registration).
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Registers a new user.
     * * @param request the registration details.
     * @return the created user, without exposing the hashed password.
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        User registeredUser = authService.register(request);
        return new ResponseEntity<>(RegisterResponse.from(registeredUser), HttpStatus.CREATED);
    }

    /**
     * Authenticates a user.
     * * @param request the login credentials.
     * @return a JSON response containing the signed JWT access token.
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = authService.login(request);
        return ResponseEntity.ok(TokenResponse.of(token));
    }
}