package com.fullrepar.customer_service.repository;

import com.fullrepar.customer_service.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * Finds a customer by their email address.
     * @param email the email to search for.
     * @return an Optional containing the found customer, or empty.
     */
    Optional<Customer> findByEmail(String email);
}
