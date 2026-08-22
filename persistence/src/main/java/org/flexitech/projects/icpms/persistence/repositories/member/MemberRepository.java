package org.flexitech.projects.icpms.persistence.repositories.member;

import org.flexitech.projects.icpms.persistence.entities.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MemberRepository extends JpaRepository<Member, Long>, JpaSpecificationExecutor<Member> {
}
