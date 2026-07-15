package com.fullrepar.customer_service.dto;

import java.time.LocalDateTime;

/**
 * DTO representing the data returned to clients for a customer.
 */
public record CustomerResponse(
    Long id,
    String fullName,
    String email,
    String phone,
    String address,
    LocalDateTime createdAt
) {}
