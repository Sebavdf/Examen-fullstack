package com.fullrepar.repair_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * DTO representing an incoming request to create a repair order.
 */
public record RepairOrderRequest(
    @NotNull(message = "Customer id is required")
    Long customerId,

    @NotNull(message = "Device id is required")
    Long deviceId,

    @NotBlank(message = "Issue description is required")
    String issueDescription,

    @Positive(message = "Estimated cost must be positive")
    BigDecimal estimatedCost
) {}
