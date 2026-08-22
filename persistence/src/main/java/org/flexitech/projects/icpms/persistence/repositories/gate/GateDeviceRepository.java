package org.flexitech.projects.icpms.persistence.repositories.gate;

import java.util.List;

import org.flexitech.projects.icpms.persistence.entities.gate.GateDevice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GateDeviceRepository extends JpaRepository<GateDevice, Long> {
	List<GateDevice> findByGateIdOrderByIdAsc(Long gateId);
	long countByGateId(Long gateId);
	void deleteByGateId(Long gateId);
}