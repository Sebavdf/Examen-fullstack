package com.fullrepar.technician_service.controller;

import com.fullrepar.technician_service.dto.TechnicianRequest;
import com.fullrepar.technician_service.dto.TechnicianResponse;
import com.fullrepar.technician_service.service.TechnicianService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/technicians")
@RequiredArgsConstructor
public class TechnicianController {

    private final TechnicianService technicianService;

    @PostMapping
    public ResponseEntity<TechnicianResponse> create(@Valid @RequestBody TechnicianRequest request) {
        return new ResponseEntity<>(technicianService.create(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TechnicianResponse>> findAll() {
        return ResponseEntity.ok(technicianService.findAll());
    }

    @GetMapping("/active")
    public ResponseEntity<List<TechnicianResponse>> findActive() {
        return ResponseEntity.ok(technicianService.findActive());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TechnicianResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(technicianService.findById(id));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<TechnicianResponse> deactivate(@PathVariable Long id) {
        return ResponseEntity.ok(technicianService.deactivate(id));
    }
}
