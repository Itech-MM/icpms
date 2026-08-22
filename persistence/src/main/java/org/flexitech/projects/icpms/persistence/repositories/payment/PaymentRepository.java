package org.flexitech.projects.icpms.persistence.repositories.payment;

import java.util.Optional;

import org.flexitech.projects.icpms.persistence.entities.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PaymentRepository extends JpaRepository<Payment, Long>, JpaSpecificationExecutor<Payment> {
	Optional<Payment> findBySessionId(Long sessionId);
}
