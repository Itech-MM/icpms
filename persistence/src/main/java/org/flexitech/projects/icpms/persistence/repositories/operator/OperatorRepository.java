package org.flexitech.projects.icpms.persistence.repositories.operator;

import java.util.Optional;

import org.flexitech.projects.icpms.persistence.entities.operator.Operator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OperatorRepository extends JpaRepository<Operator, Long>, JpaSpecificationExecutor<Operator> {
	Optional<Operator> findByUsernameIgnoreCase(String username);
}
