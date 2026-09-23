package org.flexitech.projects.icpms.admin.controller;

import org.flexitech.projects.icpms.common.CommonConstants;
import org.flexitech.projects.icpms.common.MenuCodeConstants;
import org.flexitech.projects.icpms.dto.api.request.member.MemberPlanRequest;
import org.flexitech.projects.icpms.dto.member.MemberPlanDTO;
import org.flexitech.projects.icpms.service.member.MemberPlanService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/members/plans")
public class MemberPlanController {

	private final MemberPlanService memberPlanService;

	public MemberPlanController(MemberPlanService memberPlanService) {
		this.memberPlanService = memberPlanService;
	}

	@GetMapping
	@PreAuthorize("@menuSecurity.hasMenuAccess('" + MenuCodeConstants.MENU_MEMBER_PLANS
			+ "') or @menuSecurity.hasMenuView('" + MenuCodeConstants.MENU_MEMBER_PLANS + "')")
	public String list(Model model) {
		model.addAttribute("plans", memberPlanService.getAllPlans());
		model.addAttribute("pageTitle", "Member Plans");
		model.addAttribute("activeMenu", "member-plans");
		return "members/plans/list";
	}

	@GetMapping("/new")
	@PreAuthorize("@menuSecurity.hasMenuEdit('" + MenuCodeConstants.MENU_MEMBER_PLANS + "')")
	public String createForm(Model model) {
		model.addAttribute("planRequest", new MemberPlanRequest());
		model.addAttribute("planId", null);
		model.addAttribute("pageTitle", "New Member Plan");
		model.addAttribute("activeMenu", "member-plans");
		return "members/plans/form";
	}

	@GetMapping("/{id}/edit")
	@PreAuthorize("@menuSecurity.hasMenuEdit('" + MenuCodeConstants.MENU_MEMBER_PLANS + "')")
	public String editForm(@PathVariable Long id, Model model) {
		MemberPlanDTO dto = memberPlanService.getPlanById(id);
		MemberPlanRequest request = new MemberPlanRequest();
		request.setCode(dto.getCode());
		request.setName(dto.getName());
		request.setDescription(dto.getDescription());
		request.setPrice(dto.getPrice());
		request.setDurationDays(dto.getDurationDays());
		request.setGrantedBalance(dto.getGrantedBalance());
		request.setDiscountPercent(dto.getDiscountPercent());
		request.setFreeMinutes(dto.getFreeMinutes());
		request.setMaxVehicles(dto.getMaxVehicles());
		request.setIsActive(dto.getIsActive());
		request.setExtraFeatures(dto.getExtraFeatures());
		model.addAttribute("planRequest", request);
		model.addAttribute("planId", id);
		model.addAttribute("pageTitle", "Edit Member Plan");
		model.addAttribute("activeMenu", "member-plans");
		return "members/plans/form";
	}

	@PostMapping
	@PreAuthorize("@menuSecurity.hasMenuEdit('" + MenuCodeConstants.MENU_MEMBER_PLANS + "')")
	public String create(@Valid @ModelAttribute("planRequest") MemberPlanRequest request, BindingResult bindingResult,
			Model model, RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("planId", null);
			model.addAttribute("pageTitle", "New Member Plan");
			model.addAttribute("activeMenu", "member-plans");
			return "members/plans/form";
		}
		try {
			memberPlanService.createPlan(request);
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_SUCCESS_MESSAGE,
					"Member plan created successfully.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_ERROR_MESSAGE,
					"Error creating member plan: " + e.getMessage());
		}
		return "redirect:/members/plans";
	}

	@PostMapping("/{id}")
	@PreAuthorize("@menuSecurity.hasMenuEdit('" + MenuCodeConstants.MENU_MEMBER_PLANS + "')")
	public String update(@PathVariable Long id, @Valid @ModelAttribute("planRequest") MemberPlanRequest request,
			BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("planId", id);
			model.addAttribute("pageTitle", "Edit Member Plan");
			model.addAttribute("activeMenu", "member-plans");
			return "members/plans/form";
		}
		try {
			memberPlanService.updatePlan(id, request);
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_SUCCESS_MESSAGE,
					"Member plan updated successfully.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_ERROR_MESSAGE,
					"Error updating member plan: " + e.getMessage());
		}
		return "redirect:/members/plans";
	}

	@PostMapping("/{id}/deactivate")
	@PreAuthorize("@menuSecurity.hasMenuDelete('" + MenuCodeConstants.MENU_MEMBER_PLANS + "')")
	public String deactivate(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		try {
			memberPlanService.deactivatePlan(id);
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_SUCCESS_MESSAGE,
					"Member plan deactivated successfully.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_ERROR_MESSAGE,
					"Error deactivating member plan: " + e.getMessage());
		}
		return "redirect:/members/plans";
	}
}