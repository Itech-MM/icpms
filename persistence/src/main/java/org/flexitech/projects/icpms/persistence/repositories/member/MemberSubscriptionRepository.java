package org.flexitech.projects.icpms.persistence.repositories.member;

import java.math.BigDecimal;
import java.util.Optional;

import org.flexitech.projects.icpms.persistence.entities.member.MemberSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberSubscriptionRepository extends JpaRepository<MemberSubscription, Long>, JpaSpecificationExecutor<MemberSubscription> {

	Optional<MemberSubscription> findByMemberIdAndStatus(Long memberId, Integer status);

	@Modifying
	@Query("update MemberSubscription s set s.balance = s.balance - :amount, s.updatedTime = CURRENT_TIMESTAMP "
			+ "where s.id = :id and s.status = 1 and s.endDate >= CURRENT_TIMESTAMP and s.balance >= :amount")
	int deductBalance(@Param("id") Long id, @Param("amount") BigDecimal amount);

	@Modifying
	@Query("update MemberSubscription s set s.balance = s.balance + :amount, s.updatedTime = CURRENT_TIMESTAMP where s.id = :id")
	int creditBalance(@Param("id") Long id, @Param("amount") BigDecimal amount);

	@Modifying
	@Query("update MemberSubscription s set s.status = 2, s.updatedTime = CURRENT_TIMESTAMP where s.status = 1 and s.endDate < CURRENT_TIMESTAMP")
	int expireOverdueSubscriptions();
}