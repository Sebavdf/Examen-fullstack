package com.fullrepar.customer_service.service;

import com.fullrepar.customer_service.dto.CustomerRequest;
import com.fullrepar.customer_service.dto.CustomerResponse;
import com.fullrepar.customer_service.exception.BusinessException;
import com.fullrepar.customer_service.exception.ResourceNotFoundException;
import com.fullrepar.customer_service.model.Customer;
import com.fullrepar.customer_service.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class handling business logic for customer management.
 * Validates uniqueness rules and maps entities to DTOs.
 */
@Service
@RequiredArgsConstructor
public class CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);
    private final CustomerRepository customerRepository;

    /**
     * Creates a new customer after validating that the email is not already registered.
     *
     * @param request the customer data.
     * @return the created customer as a response DTO.
     */
    @Transactional
    public CustomerResponse create(CustomerRequest request) {
        log.info("Attempting to create customer with email: {}", request.email());

        if (customerRepository.findByEmail(request.email()).isPresent()) {
            log.warn("Creation failed: email '{}' already registered", request.email());
            throw new BusinessException("Email is already registered to another customer");
        }

        Customer customer = Customer.builder()
                .fullName(request.fullName())
                .email(request.email())
                .phone(request.phone())
                .address(request.address())
                .build();

        Customer saved = customerRepository.save(customer);
        log.info("Customer '{}' created with ID: {}", saved.getFullName(), saved.getId());
        return toResponse(saved);
    }

    /**
     * Retrieves all registered customers.
     *
     * @return list of customer response DTOs.
     */
    public List<CustomerResponse> findAll() {
        return customerRepository.findAll().stream().map(this::toResponse).toList();
    }

    /**
     * Retrieves a customer by its identifier.
     *
     * @param id the customer identifier.
     * @return the customer response DTO.
     */
    public CustomerResponse findById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        return toResponse(customer);
    }

    /**
     * Updates an existing customer's information.
     *
     * @param id the customer identifier.
     * @param request the updated customer data.
     * @return the updated customer response DTO.
     */
    @Transactional
    public CustomerResponse update(Long id, CustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        customerRepository.findByEmail(request.email())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new BusinessException("Email is already registered to another customer");
                });

        customer.setFullName(request.fullName());
        customer.setEmail(request.email());
        customer.setPhone(request.phone());
        customer.setAddress(request.address());

        Customer updated = customerRepository.save(customer);
        log.info("Customer with ID {} updated", id);
        return toResponse(updated);
    }

    /**
     * Deletes a customer by its identifier.
     *
     * @param id the customer identifier.
     */
    @Transactional
    public void delete(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        customerRepository.delete(customer);
        log.info("Customer with ID {} deleted", id);
    }

    /**
     * Checks whether a customer exists, used by other microservices' validation flows.
     *
     * @param id the customer identifier.
     * @return true if the customer exists.
     */
    public boolean existsById(Long id) {
        return customerRepository.existsById(id);
    }

    private CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getFullName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getAddress(),
                customer.getCreatedAt()
        );
    }
}
