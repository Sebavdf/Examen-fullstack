package com.fullrepar.auth_service.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO representing a login request.
 */
public record LoginRequest(
    @NotBlank(message = "Username is required")
    String username,

    @NotBlank(message = "Password is required")
    String password
) {}