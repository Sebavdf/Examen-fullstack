package com.fullrepar.review_service.dto;

import java.time.LocalDateTime;

public record ReviewResponse(
    Long id, Long repairOrderId, Long customerId, Integer rating, String comment, LocalDateTime createdAt
) {}
