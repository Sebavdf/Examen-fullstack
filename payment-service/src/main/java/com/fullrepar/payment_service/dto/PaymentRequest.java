package com.fullrepar.payment_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record PaymentRequest(
    @NotNull(message = "Repair order id is required") Long repairOrderId,
    @NotNull(message = "Amount is required") @Positive(message = "Amount must be positive") BigDecimal amount,
    @NotBlank(message = "Method is required") String method
) {}
