package org.flexitech.projects.icpms.persistence.repositories.session;

import java.util.List;
import java.util.Optional;

import org.flexitech.projects.icpms.persistence.entities.session.ParkingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ParkingSessionRepository extends JpaRepository<ParkingSession, Long>, JpaSpecificationExecutor<ParkingSession> {
	List<ParkingSession> findByVehicleIdAndStatus(Long vehicleId, Integer status);
	Optional<ParkingSession> findFirstByVehicleIdAndStatusOrderByEntryTimeDesc(Long vehicleId, Integer status);
	long countByStatus(Integer status);
}
