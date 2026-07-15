package com.fullrepar.inventory_service.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * DTO representing an incoming request to create or update a spare part.
 */
public record PartRequest(
    @NotBlank(message = "Name is required") String name,
    @NotBlank(message = "SKU is required") String sku,
    @NotNull(message = "Stock is required") @PositiveOrZero(message = "Stock cannot be negative") Integer stock,
    @NotNull(message = "Unit price is required") @Positive(message = "Unit price must be positive") BigDecimal unitPrice
) {}
