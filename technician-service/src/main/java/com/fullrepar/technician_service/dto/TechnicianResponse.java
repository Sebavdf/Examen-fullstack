package com.fullrepar.technician_service.dto;

import java.time.LocalDateTime;

public record TechnicianResponse(
    Long id, String fullName, String specialty, Boolean active, LocalDateTime createdAt
) {}
