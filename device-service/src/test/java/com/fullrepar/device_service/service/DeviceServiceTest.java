package com.fullrepar.device_service.service;

import com.fullrepar.device_service.client.CustomerClient;
import com.fullrepar.device_service.dto.DeviceRequest;
import com.fullrepar.device_service.dto.DeviceResponse;
import com.fullrepar.device_service.exception.BusinessException;
import com.fullrepar.device_service.exception.ResourceNotFoundException;
import com.fullrepar.device_service.model.Device;
import com.fullrepar.device_service.repository.DeviceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private CustomerClient customerClient;

    @InjectMocks
    private DeviceService deviceService;

    private DeviceRequest request;
    private Device device;

    @BeforeEach
    void setUp() {
        request = new DeviceRequest(1L, "Samsung", "Galaxy S22", "SN-0001", "SMARTPHONE");
        device = Device.builder()
                .id(1L).customerId(1L).brand("Samsung").model("Galaxy S22")
                .serialNumber("SN-0001").deviceType("SMARTPHONE").build();
    }

    @Test
    void create_ShouldReturnResponse_WhenCustomerExistsAndSerialIsUnique() {
        // Given
        doNothing().when(customerClient).validateCustomerExists(1L);
        when(deviceRepository.findBySerialNumber("SN-0001")).thenReturn(Optional.empty());
        when(deviceRepository.save(any(Device.class))).thenReturn(device);

        // When
        DeviceResponse response = deviceService.create(request);

        // Then
        assertNotNull(response);
        assertEquals("SN-0001", response.serialNumber());
        verify(customerClient, times(1)).validateCustomerExists(1L);
    }

    @Test
    void create_ShouldThrowBusinessException_WhenCustomerDoesNotExist() {
        // Given
        doThrow(new BusinessException("Customer with id 1 does not exist"))
                .when(customerClient).validateCustomerExists(1L);

        // When / Then
        assertThrows(BusinessException.class, () -> deviceService.create(request));
        verify(deviceRepository, never()).save(any(Device.class));
    }

    @Test
    void create_ShouldThrowBusinessException_WhenSerialAlreadyRegistered() {
        // Given
        doNothing().when(customerClient).validateCustomerExists(1L);
        when(deviceRepository.findBySerialNumber("SN-0001")).thenReturn(Optional.of(device));

        // When / Then
        assertThrows(BusinessException.class, () -> deviceService.create(request));
    }

    @Test
    void findById_ShouldThrowResourceNotFoundException_WhenDeviceDoesNotExist() {
        when(deviceRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> deviceService.findById(99L));
    }
}
