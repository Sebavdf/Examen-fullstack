package com.fullrepar.repair_service.service;

import com.fullrepar.repair_service.client.CustomerClient;
import com.fullrepar.repair_service.client.DeviceClient;
import com.fullrepar.repair_service.dto.RepairOrderRequest;
import com.fullrepar.repair_service.dto.RepairOrderResponse;
import com.fullrepar.repair_service.exception.BusinessException;
import com.fullrepar.repair_service.exception.ResourceNotFoundException;
import com.fullrepar.repair_service.model.RepairOrder;
import com.fullrepar.repair_service.model.RepairStatus;
import com.fullrepar.repair_service.repository.RepairOrderRepository;
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
class RepairOrderServiceTest {

    @Mock
    private RepairOrderRepository repairOrderRepository;
    @Mock
    private CustomerClient customerClient;
    @Mock
    private DeviceClient deviceClient;

    @InjectMocks
    private RepairOrderService repairOrderService;

    private RepairOrder order;

    @BeforeEach
    void setUp() {
        order = RepairOrder.builder()
                .id(1L).customerId(1L).deviceId(1L)
                .issueDescription("Pantalla rota")
                .status(RepairStatus.RECEIVED)
                .estimatedCost(new BigDecimal("45000"))
                .build();
    }

    @Test
    void create_ShouldReturnResponse_WhenCustomerAndDeviceExist() {
        RepairOrderRequest request = new RepairOrderRequest(1L, 1L, "Pantalla rota", new BigDecimal("45000"));
        doNothing().when(customerClient).validateCustomerExists(1L);
        doNothing().when(deviceClient).validateDeviceExists(1L);
        when(repairOrderRepository.save(any(RepairOrder.class))).thenReturn(order);

        RepairOrderResponse response = repairOrderService.create(request);

        assertNotNull(response);
        assertEquals(RepairStatus.RECEIVED, response.status());
    }

    @Test
    void updateStatus_ShouldAllowValidTransition_ReceivedToInProgress() {
        when(repairOrderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(repairOrderRepository.save(any(RepairOrder.class))).thenAnswer(inv -> inv.getArgument(0));

        RepairOrderResponse response = repairOrderService.updateStatus(1L, RepairStatus.IN_PROGRESS);

        assertEquals(RepairStatus.IN_PROGRESS, response.status());
    }

    @Test
    void updateStatus_ShouldRejectInvalidTransition_ReceivedToDelivered() {
        when(repairOrderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(BusinessException.class, () -> repairOrderService.updateStatus(1L, RepairStatus.DELIVERED));
        verify(repairOrderRepository, never()).save(any(RepairOrder.class));
    }

    @Test
    void updateStatus_ShouldThrowResourceNotFoundException_WhenOrderDoesNotExist() {
        when(repairOrderRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> repairOrderService.updateStatus(99L, RepairStatus.IN_PROGRESS));
    }

    @Test
    void isCompleted_ShouldReturnTrue_WhenStatusIsCompletedOrDelivered() {
        order.setStatus(RepairStatus.COMPLETED);
        when(repairOrderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertTrue(repairOrderService.isCompleted(1L));
    }

    @Test
    void isCompleted_ShouldReturnFalse_WhenOrderNotFound() {
        when(repairOrderRepository.findById(50L)).thenReturn(Optional.empty());
        assertFalse(repairOrderService.isCompleted(50L));
    }
}
