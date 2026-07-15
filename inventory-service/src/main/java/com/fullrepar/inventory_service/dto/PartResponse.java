package com.fullrepar.inventory_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PartResponse(
    Long id, String name, String sku, Integer stock, BigDecimal unitPrice, LocalDateTime createdAt
) {}
