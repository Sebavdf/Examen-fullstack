package com.fullrepar.repair_service.repository;

import com.fullrepar.repair_service.model.RepairOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepairOrderRepository extends JpaRepository<RepairOrder, Long> {

    /**
     * Finds all repair orders belonging to a given customer.
     * @param customerId the owning customer's id.
     * @return list of repair orders.
     */
    List<RepairOrder> findByCustomerId(Long customerId);
}
