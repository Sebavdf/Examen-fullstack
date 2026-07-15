package com.fullrepar.inventory_service.service;

import com.fullrepar.inventory_service.dto.PartRequest;
import com.fullrepar.inventory_service.dto.PartResponse;
import com.fullrepar.inventory_service.exception.BusinessException;
import com.fullrepar.inventory_service.exception.ResourceNotFoundException;
import com.fullrepar.inventory_service.model.Part;
import com.fullrepar.inventory_service.repository.PartRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class handling business logic for spare parts inventory.
 * Enforces the rule that stock can never go below zero.
 */
@Service
@RequiredArgsConstructor
public class PartService {

    private static final Logger log = LoggerFactory.getLogger(PartService.class);
    private final PartRepository partRepository;

    @Transactional
    public PartResponse create(PartRequest request) {
        if (partRepository.findBySku(request.sku()).isPresent()) {
            throw new BusinessException("SKU is already registered");
        }
        Part part = Part.builder()
                .name(request.name())
                .sku(request.sku())
                .stock(request.stock())
                .unitPrice(request.unitPrice())
                .build();
        Part saved = partRepository.save(part);
        log.info("Part '{}' created with ID: {}", saved.getSku(), saved.getId());
        return toResponse(saved);
    }

    public List<PartResponse> findAll() {
        return partRepository.findAll().stream().map(this::toResponse).toList();
    }

    public PartResponse findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    /**
     * Adjusts the stock of a part. A negative quantity consumes stock (e.g. used
     * in a repair), a positive quantity restocks. Rejects the operation if the
     * resulting stock would be negative.
     *
     * @param id the part id.
     * @param quantity the adjustment amount (can be negative).
     * @return the updated part.
     */
    @Transactional
    public PartResponse adjustStock(Long id, Integer quantity) {
        Part part = getOrThrow(id);
        int newStock = part.getStock() + quantity;

        if (newStock < 0) {
            log.warn("Stock adjustment rejected for part {}: would result in negative stock", id);
            throw new BusinessException("Insufficient stock for part: " + part.getName());
        }

        part.setStock(newStock);
        Part updated = partRepository.save(part);
        log.info("Part {} stock adjusted by {}. New stock: {}", id, quantity, newStock);
        return toResponse(updated);
    }

    @Transactional
    public void delete(Long id) {
        Part part = getOrThrow(id);
        partRepository.delete(part);
        log.info("Part with ID {} deleted", id);
    }

    private Part getOrThrow(Long id) {
        return partRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Part not found with id: " + id));
    }

    private PartResponse toResponse(Part part) {
        return new PartResponse(part.getId(), part.getName(), part.getSku(), part.getStock(),
                part.getUnitPrice(), part.getCreatedAt());
    }
}
