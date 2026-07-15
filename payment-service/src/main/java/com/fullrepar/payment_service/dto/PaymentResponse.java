package com.fullrepar.payment_service.dto;

import com.fullrepar.payment_service.model.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
    Long id, Long repairOrderId, BigDecimal amount, String method,
    PaymentStatus status, LocalDateTime paidAt, LocalDateTime createdAt
) {}
