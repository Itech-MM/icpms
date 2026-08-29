package org.flexitech.projects.icpms.persistence.repositories.operator;

import java.util.Optional;

import org.flexitech.projects.icpms.persistence.entities.operator.OperatorShift;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperatorShiftRepository extends JpaRepository<OperatorShift, Long>{
	Optional<OperatorShift> findFirstByOperatorIdAndGateIdAndShiftStatus(Long operatorId, Long gateId, Integer shiftStatus);
	Optional<OperatorShift> findByCode(String code);
}
