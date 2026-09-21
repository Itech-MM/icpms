package org.flexitech.projects.icpms.persistence.repositories.operator;

import java.util.Optional;

import org.flexitech.projects.icpms.persistence.entities.operator.Operator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OperatorRepository extends JpaRepository<Operator, Long>, JpaSpecificationExecutor<Operator> {
	Optional<Operator> findByUsernameIgnoreCase(String username);
	
	boolean existsByRfidToken(String rfidToken);
	boolean existsByQrCodeToken(String qrCodeToken);
	boolean existsByStripeToken(String stripeToken);
	boolean existsByPinPassword(String pinPassword);

	boolean existsByPinPasswordAndIdNot(String pinPassword, Long id);
	
	boolean existsByUsername(String username);
	boolean existsByUsernameAndIdNot(String username, Long id);
	
	Optional<Operator> findByRfidToken(String rfidToken);

	Optional<Operator> findByQrCodeToken(String qrCodeToken);

	Optional<Operator> findByStripeToken(String stripeToken);

	Optional<Operator> findByPinPassword(String pinPassword);
}
