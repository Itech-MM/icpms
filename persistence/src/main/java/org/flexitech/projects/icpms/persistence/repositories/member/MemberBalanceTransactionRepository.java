package org.flexitech.projects.icpms.persistence.repositories.member;

import java.util.List;

import org.flexitech.projects.icpms.persistence.entities.member.MemberBalanceTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberBalanceTransactionRepository extends JpaRepository<MemberBalanceTransaction, Long> {

	List<MemberBalanceTransaction> findBySubscriptionIdOrderByCreatedTimeDesc(Long subscriptionId);
}