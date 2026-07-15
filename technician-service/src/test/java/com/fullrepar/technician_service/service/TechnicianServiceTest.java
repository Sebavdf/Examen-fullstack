package com.fullrepar.technician_service.service;

import com.fullrepar.technician_service.exception.BusinessException;
import com.fullrepar.technician_service.exception.ResourceNotFoundException;
import com.fullrepar.technician_service.model.Technician;
import com.fullrepar.technician_service.repository.TechnicianRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TechnicianServiceTest {

    @Mock
    private TechnicianRepository technicianRepository;

    @InjectMocks
    private TechnicianService technicianService;

    private Technician technician;

    @BeforeEach
    void setUp() {
        technician = Technician.builder().id(1L).fullName("Carlos Soto").specialty("SMARTPHONE").active(true).build();
    }

    @Test
    void deactivate_ShouldSetActiveFalse_WhenTechnicianIsActive() {
        when(technicianRepository.findById(1L)).thenReturn(Optional.of(technician));
        when(technicianRepository.save(any(Technician.class))).thenAnswer(inv -> inv.getArgument(0));

        var response = technicianService.deactivate(1L);

        assertFalse(response.active());
    }

    @Test
    void deactivate_ShouldThrowBusinessException_WhenAlreadyInactive() {
        technician.setActive(false);
        when(technicianRepository.findById(1L)).thenReturn(Optional.of(technician));

        assertThrows(BusinessException.class, () -> technicianService.deactivate(1L));
    }

    @Test
    void findById_ShouldThrowResourceNotFoundException_WhenNotFound() {
        when(technicianRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> technicianService.findById(99L));
    }
}
