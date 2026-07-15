package com.fullrepar.technician_service.service;

import com.fullrepar.technician_service.dto.TechnicianRequest;
import com.fullrepar.technician_service.dto.TechnicianResponse;
import com.fullrepar.technician_service.exception.BusinessException;
import com.fullrepar.technician_service.exception.ResourceNotFoundException;
import com.fullrepar.technician_service.model.Technician;
import com.fullrepar.technician_service.repository.TechnicianRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class handling business logic for technician management.
 */
@Service
@RequiredArgsConstructor
public class TechnicianService {

    private static final Logger log = LoggerFactory.getLogger(TechnicianService.class);
    private final TechnicianRepository technicianRepository;

    @Transactional
    public TechnicianResponse create(TechnicianRequest request) {
        Technician technician = Technician.builder()
                .fullName(request.fullName())
                .specialty(request.specialty())
                .active(true)
                .build();
        Technician saved = technicianRepository.save(technician);
        log.info("Technician '{}' created with ID: {}", saved.getFullName(), saved.getId());
        return toResponse(saved);
    }

    public List<TechnicianResponse> findAll() {
        return technicianRepository.findAll().stream().map(this::toResponse).toList();
    }

    public List<TechnicianResponse> findActive() {
        return technicianRepository.findByActiveTrue().stream().map(this::toResponse).toList();
    }

    public TechnicianResponse findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    /**
     * Deactivates a technician. A technician already inactive cannot be deactivated again.
     */
    @Transactional
    public TechnicianResponse deactivate(Long id) {
        Technician technician = getOrThrow(id);
        if (!technician.getActive()) {
            throw new BusinessException("Technician is already inactive");
        }
        technician.setActive(false);
        Technician updated = technicianRepository.save(technician);
        log.info("Technician {} deactivated", id);
        return toResponse(updated);
    }

    private Technician getOrThrow(Long id) {
        return technicianRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Technician not found with id: " + id));
    }

    private TechnicianResponse toResponse(Technician t) {
        return new TechnicianResponse(t.getId(), t.getFullName(), t.getSpecialty(), t.getActive(), t.getCreatedAt());
    }
}
