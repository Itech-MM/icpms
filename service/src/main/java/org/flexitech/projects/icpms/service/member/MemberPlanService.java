package org.flexitech.projects.icpms.service.member;

import java.util.List;

import org.flexitech.projects.icpms.dto.api.request.member.MemberPlanRequest;
import org.flexitech.projects.icpms.dto.member.MemberPlanDTO;

public interface MemberPlanService {

	MemberPlanDTO createPlan(MemberPlanRequest request);

	MemberPlanDTO updatePlan(Long id, MemberPlanRequest request);

	MemberPlanDTO getPlanById(Long id);

	List<MemberPlanDTO> getAllPlans();

	List<MemberPlanDTO> getActivePlans();

	void deactivatePlan(Long id);
}