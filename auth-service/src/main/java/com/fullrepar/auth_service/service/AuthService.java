package com.fullrepar.auth_service.service;

import com.fullrepar.auth_service.dto.LoginRequest;
import com.fullrepar.auth_service.dto.RegisterRequest;
import com.fullrepar.auth_service.exception.BusinessException;
import com.fullrepar.auth_service.model.User;
import com.fullrepar.auth_service.repository.UserRepository;
import com.fullrepar.auth_service.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class handling authentication business logic.
 * Manages user registration and validation.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * Registers a new user in the system after validating uniqueness.
     * * @param request the registration details.
     * @return the saved User entity.
     */
    @Transactional
    public User register(RegisterRequest request) {
        log.info("Attempting to register user with username: {}", request.username());

        // Rule 1: Check if username already exists
        if (userRepository.findByUsername(request.username()).isPresent()) {
            log.warn("Registration failed: Username '{}' is already taken", request.username());
            throw new BusinessException("Username is already taken");
        }

        // Rule 2: Check if email already exists
        if (userRepository.findByEmail(request.email()).isPresent()) {
            log.warn("Registration failed: Email '{}' is already registered", request.email());
            throw new BusinessException("Email is already registered");
        }

        // Build and save the entity, storing a BCrypt hash instead of the plain password
        User newUser = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(request.role().toUpperCase())
                .build();

        User savedUser = userRepository.save(newUser);
        log.info("User '{}' successfully registered with ID: {}", savedUser.getUsername(), savedUser.getId());
        
        return savedUser;
    }

    /**
     * Authenticates an existing user and issues a signed JWT access token.
     * * @param request the login credentials.
     * @return a signed JWT that downstream services can validate.
     */
    public String login(LoginRequest request) {
        log.info("Attempting login for user: {}", request.username());

        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> {
                    log.warn("Login failed: User '{}' not found", request.username());
                    return new BusinessException("Invalid username or password");
                });

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            log.warn("Login failed: Incorrect password for user '{}'", request.username());
            throw new BusinessException("Invalid username or password");
        }

        log.info("User '{}' successfully logged in", user.getUsername());
        return jwtUtil.generateToken(user.getUsername(), user.getRole());
    }
}