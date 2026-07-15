package com.fullrepar.inventory_service.service;

import com.fullrepar.inventory_service.dto.PartRequest;
import com.fullrepar.inventory_service.exception.BusinessException;
import com.fullrepar.inventory_service.model.Part;
import com.fullrepar.inventory_service.repository.PartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartServiceTest {

    @Mock
    private PartRepository partRepository;

    @InjectMocks
    private PartService partService;

    private Part part;

    @BeforeEach
    void setUp() {
        part = Part.builder().id(1L).name("Pantalla").sku("SKU-1").stock(10)
                .unitPrice(new BigDecimal("38000")).build();
    }

    @Test
    void create_ShouldThrowBusinessException_WhenSkuAlreadyExists() {
        when(partRepository.findBySku("SKU-1")).thenReturn(Optional.of(part));
        PartRequest request = new PartRequest("Pantalla", "SKU-1", 10, new BigDecimal("38000"));
        assertThrows(BusinessException.class, () -> partService.create(request));
    }

    @Test
    void adjustStock_ShouldReduceStock_WhenEnoughAvailable() {
        when(partRepository.findById(1L)).thenReturn(Optional.of(part));
        when(partRepository.save(any(Part.class))).thenAnswer(inv -> inv.getArgument(0));

        var response = partService.adjustStock(1L, -3);

        assertEquals(7, response.stock());
    }

    @Test
    void adjustStock_ShouldThrowBusinessException_WhenResultingStockWouldBeNegative() {
        when(partRepository.findById(1L)).thenReturn(Optional.of(part));
        assertThrows(BusinessException.class, () -> partService.adjustStock(1L, -20));
        verify(partRepository, never()).save(any(Part.class));
    }
}
