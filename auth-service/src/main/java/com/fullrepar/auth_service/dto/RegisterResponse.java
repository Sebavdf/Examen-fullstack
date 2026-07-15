package com.fullrepar.auth_service.dto;

import com.fullrepar.auth_service.model.User;

/**
 * DTO representing the public view of a newly registered user.
 * Deliberately excludes the password hash from the API response.
 */
public record RegisterResponse(
    Long id,
    String username,
    String email,
    String role
) {
    public static RegisterResponse from(User user) {
        return new RegisterResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole());
    }
}
