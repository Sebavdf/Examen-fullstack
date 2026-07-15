package com.fullrepar.review_service.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReviewRequest(
    @NotNull(message = "Repair order id is required") Long repairOrderId,
    @NotNull(message = "Customer id is required") Long customerId,
    @NotNull(message = "Rating is required") @Min(1) @Max(5) Integer rating,
    @Size(max = 500, message = "Comment must be at most 500 characters") String comment
) {}
