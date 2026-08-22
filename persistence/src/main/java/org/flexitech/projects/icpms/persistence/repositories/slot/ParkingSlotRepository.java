package org.flexitech.projects.icpms.persistence.repositories.slot;

import java.util.List;

import org.flexitech.projects.icpms.persistence.entities.slot.ParkingSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Long>, JpaSpecificationExecutor<ParkingSlot> {
	List<ParkingSlot> findBySiteId(Long siteId);
	long countBySiteIdAndStatus(Long siteId, Integer status);
	long countByStatus(Integer status);
}
