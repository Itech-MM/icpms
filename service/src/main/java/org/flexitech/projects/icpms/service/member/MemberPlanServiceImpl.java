package org.flexitech.projects.icpms.service.member;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.flexitech.projects.icpms.common.exceptions.MemberPlanNotFoundException;
import org.flexitech.projects.icpms.dto.api.request.member.MemberPlanRequest;
import org.flexitech.projects.icpms.dto.member.MemberPlanDTO;
import org.flexitech.projects.icpms.persistence.entities.member.MemberPlan;
import org.flexitech.projects.icpms.persistence.repositories.member.MemberPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberPlanServiceImpl implements MemberPlanService {

	@Autowired
	private MemberPlanRepository memberPlanRepository;

	@Override
	@Transactional
	public MemberPlanDTO createPlan(MemberPlanRequest request) {
		MemberPlan plan = new MemberPlan();
		applyRequest(plan, request);
		plan.setStatus(1);
		if (plan.getGrantedBalance() == null) {
			plan.setGrantedBalance(BigDecimal.ZERO);
		}
		plan = memberPlanRepository.save(plan);
		return new MemberPlanDTO(plan);
	}

	@Override
	@Transactional
	public MemberPlanDTO updatePlan(Long id, MemberPlanRequest request) {
		MemberPlan plan = memberPlanRepository.findById(id)
				.orElseThrow(() -> new MemberPlanNotFoundException("Member plan not found: " + id));
		applyRequest(plan, request);
		plan = memberPlanRepository.save(plan);
		return new MemberPlanDTO(plan);
	}

	@Override
	public MemberPlanDTO getPlanById(Long id) {
		return memberPlanRepository.findById(id)
				.map(MemberPlanDTO::new)
				.orElseThrow(() -> new MemberPlanNotFoundException("Member plan not found: " + id));
	}

	@Override
	public List<MemberPlanDTO> getAllPlans() {
		return memberPlanRepository.findAll().stream().map(MemberPlanDTO::new).collect(Collectors.toList());
	}

	@Override
	public List<MemberPlanDTO> getActivePlans() {
		return memberPlanRepository.findAll().stream()
				.filter(p -> Boolean.TRUE.equals(p.getIsActive()))
				.map(MemberPlanDTO::new)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public void deactivatePlan(Long id) {
		MemberPlan plan = memberPlanRepository.findById(id)
				.orElseThrow(() -> new MemberPlanNotFoundException("Member plan not found: " + id));
		plan.setIsActive(false);
		plan.setStatus(2);
		memberPlanRepository.save(plan);
	}

	private void applyRequest(MemberPlan plan, MemberPlanRequest request) {
		plan.setCode(request.getCode());
		plan.setName(request.getName());
		plan.setDescription(request.getDescription());
		plan.setPrice(request.getPrice());
		plan.setDurationDays(request.getDurationDays());
		plan.setGrantedBalance(request.getGrantedBalance());
		plan.setDiscountPercent(request.getDiscountPercent() != null ? request.getDiscountPercent() : BigDecimal.ZERO);
		plan.setFreeMinutes(request.getFreeMinutes() != null ? request.getFreeMinutes() : 0);
		plan.setMaxVehicles(request.getMaxVehicles() != null ? request.getMaxVehicles() : 1);
		plan.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
		plan.setExtraFeatures(request.getExtraFeatures());
	}
}