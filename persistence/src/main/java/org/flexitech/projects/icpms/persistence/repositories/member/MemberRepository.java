package org.flexitech.projects.icpms.persistence.repositories.member;

import org.flexitech.projects.icpms.persistence.entities.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long>, JpaSpecificationExecutor<Member> {
	@Modifying
	@Query("update Member m set m.currentSubscription = null where m.currentSubscription.id = :subscriptionId")
	int clearCurrentSubscription(@Param("subscriptionId") Long subscriptionId);

	@Modifying
	@Query("update Member m set m.currentSubscription = null where m.currentSubscription.status = 2")
	int clearExpiredCurrentSubscriptions();
}
