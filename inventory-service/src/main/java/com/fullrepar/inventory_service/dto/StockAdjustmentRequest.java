package com.fullrepar.inventory_service.dto;

import jakarta.validation.constraints.NotNull;

/**
 * DTO representing a request to consume or restock units of a part.
 * A positive quantity restocks, a negative quantity consumes units.
 */
public record StockAdjustmentRequest(
    @NotNull(message = "Quantity is required") Integer quantity
) {}
