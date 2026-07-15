package com.fullrepar.technician_service.repository;

import com.fullrepar.technician_service.model.Technician;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TechnicianRepository extends JpaRepository<Technician, Long> {
    List<Technician> findByActiveTrue();
    List<Technician> findBySpecialty(String specialty);
}
