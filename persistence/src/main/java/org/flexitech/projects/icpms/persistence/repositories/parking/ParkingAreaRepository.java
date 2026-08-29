package org.flexitech.projects.icpms.persistence.repositories.parking;

import java.util.List;

import org.flexitech.projects.icpms.persistence.entities.parking.ParkingArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ParkingAreaRepository extends JpaRepository<ParkingArea, Long>, JpaSpecificationExecutor<ParkingArea>{
	List<ParkingArea> findByStatus(Integer status);
}
