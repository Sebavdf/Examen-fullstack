package com.fullrepar.customer_service.service;

import com.fullrepar.customer_service.dto.CustomerRequest;
import com.fullrepar.customer_service.dto.CustomerResponse;
import com.fullrepar.customer_service.exception.BusinessException;
import com.fullrepar.customer_service.exception.ResourceNotFoundException;
import com.fullrepar.customer_service.model.Customer;
import com.fullrepar.customer_service.repository.CustomerRepository;
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
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private CustomerRequest request;
    private Customer customer;

    @BeforeEach
    void setUp() {
        request = new CustomerRequest("Juan Perez", "juan@example.com", "+56911111111", "Calle 123");
        customer = Customer.builder()
                .id(1L)
                .fullName("Juan Perez")
                .email("juan@example.com")
                .phone("+56911111111")
                .address("Calle 123")
                .build();
    }

    @Test
    void create_ShouldReturnResponse_WhenEmailIsNotTaken() {
        // Given
        when(customerRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        // When
        CustomerResponse response = customerService.create(request);

        // Then
        assertNotNull(response);
        assertEquals("Juan Perez", response.fullName());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void create_ShouldThrowBusinessException_WhenEmailAlreadyExists() {
        // Given
        when(customerRepository.findByEmail(request.email())).thenReturn(Optional.of(customer));

        // When / Then
        assertThrows(BusinessException.class, () -> customerService.create(request));
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void findById_ShouldThrowResourceNotFoundException_WhenCustomerDoesNotExist() {
        // Given
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> customerService.findById(99L));
    }

    @Test
    void findById_ShouldReturnCustomer_WhenExists() {
        // Given
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        // When
        CustomerResponse response = customerService.findById(1L);

        // Then
        assertEquals(1L, response.id());
        assertEquals("juan@example.com", response.email());
    }

    @Test
    void delete_ShouldThrowResourceNotFoundException_WhenCustomerDoesNotExist() {
        when(customerRepository.findById(5L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> customerService.delete(5L));
    }
}
