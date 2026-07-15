package com.fullrepar.technician_service.dto;

import jakarta.validation.constraints.NotBlank;

public record TechnicianRequest(
    @NotBlank(message = "Full name is required") String fullName,
    @NotBlank(message = "Specialty is required") String specialty
) {}
