package org.flexitech.projects.icpms.persistence.repositories.vehicle;

import java.util.Optional;

import org.flexitech.projects.icpms.persistence.entities.vehicle.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface VehicleRepository extends JpaRepository<Vehicle, Long>, JpaSpecificationExecutor<Vehicle> {
	Optional<Vehicle> findByPlateNumberIgnoreCase(String plateNumber);
}
