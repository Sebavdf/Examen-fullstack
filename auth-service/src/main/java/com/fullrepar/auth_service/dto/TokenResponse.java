package com.fullrepar.auth_service.dto;

/**
 * DTO representing a successful login response containing the JWT access token.
 */
public record TokenResponse(
    String token,
    String tokenType
) {
    public static TokenResponse of(String token) {
        return new TokenResponse(token, "Bearer");
    }
}
