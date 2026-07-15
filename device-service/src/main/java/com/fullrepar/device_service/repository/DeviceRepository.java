package com.fullrepar.device_service.repository;

import com.fullrepar.device_service.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {

    /**
     * Finds a device by its unique serial number.
     * @param serialNumber the serial number to search for.
     * @return an Optional containing the found device, or empty.
     */
    Optional<Device> findBySerialNumber(String serialNumber);

    /**
     * Finds all devices belonging to a given customer.
     * @param customerId the owning customer's id.
     * @return list of devices.
     */
    List<Device> findByCustomerId(Long customerId);
}
