package com.fullrepar.inventory_service.exception;

import java.time.LocalDateTime;

/**
 * Standardized error response structure for the API.
 */
public record ErrorResponse(
    LocalDateTime timestamp,
    int status,
    String error,
    String message,
    String path
) {}
