package com.fullrepar.inventory_service.controller;

import com.fullrepar.inventory_service.dto.PartRequest;
import com.fullrepar.inventory_service.dto.PartResponse;
import com.fullrepar.inventory_service.dto.StockAdjustmentRequest;
import com.fullrepar.inventory_service.service.PartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller exposing REST endpoints for spare parts inventory management.
 */
@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class PartController {

    private final PartService partService;

    @PostMapping
    public ResponseEntity<PartResponse> create(@Valid @RequestBody PartRequest request) {
        return new ResponseEntity<>(partService.create(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PartResponse>> findAll() {
        return ResponseEntity.ok(partService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(partService.findById(id));
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<PartResponse> adjustStock(@PathVariable Long id,
                                                      @Valid @RequestBody StockAdjustmentRequest request) {
        return ResponseEntity.ok(partService.adjustStock(id, request.quantity()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        partService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
