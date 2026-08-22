package org.flexitech.projects.icpms.persistence.repositories.gate;

import java.util.List;

import org.flexitech.projects.icpms.persistence.entities.gate.Gate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GateRepository extends JpaRepository<Gate, Long>, JpaSpecificationExecutor<Gate> {
	List<Gate> findBySiteId(Long siteId);
}
