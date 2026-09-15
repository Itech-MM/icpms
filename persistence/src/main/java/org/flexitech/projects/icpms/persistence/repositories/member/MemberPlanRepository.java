package org.flexitech.projects.icpms.persistence.repositories.member;

import java.util.Optional;

import org.flexitech.projects.icpms.persistence.entities.member.MemberPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MemberPlanRepository extends JpaRepository<MemberPlan, Long>, JpaSpecificationExecutor<MemberPlan> {

	Optional<MemberPlan> findByCode(String code);

	boolean existsByCode(String code);
}