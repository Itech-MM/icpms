package org.flexitech.projects.icpms.admin.controller;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.stream.Collectors;

import org.flexitech.projects.icpms.common.CommonConstants;
import org.flexitech.projects.icpms.common.enums.ActiveStatus;
import org.flexitech.projects.icpms.common.enums.MembershipType;
import org.flexitech.projects.icpms.common.exceptions.NoActiveSubscriptionException;
import org.flexitech.projects.icpms.dto.SearchResultDTO;
import org.flexitech.projects.icpms.dto.api.request.member.SubscribeMemberPlanRequest;
import org.flexitech.projects.icpms.dto.api.request.member.TopUpBalanceRequest;
import org.flexitech.projects.icpms.dto.member.MemberBalanceTransactionDTO;
import org.flexitech.projects.icpms.dto.member.MemberDTO;
import org.flexitech.projects.icpms.dto.member.MemberSearchDTO;
import org.flexitech.projects.icpms.dto.member.MemberSubscriptionDTO;
import org.flexitech.projects.icpms.persistence.repositories.member.MemberBalanceTransactionRepository;
import org.flexitech.projects.icpms.service.member.MemberPlanService;
import org.flexitech.projects.icpms.service.member.MemberService;
import org.flexitech.projects.icpms.service.member.MemberSubscriptionService;
import org.flexitech.projects.icpms.service.slot.ParkingSlotService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/members")
public class MemberController {

	private final MemberService memberService;
	private final ParkingSlotService slotService;
	private final MemberPlanService memberPlanService;
	private final MemberSubscriptionService memberSubscriptionService;
	private final MemberBalanceTransactionRepository memberBalanceTransactionRepository;

	public MemberController(MemberService memberService, ParkingSlotService slotService,
			MemberPlanService memberPlanService, MemberSubscriptionService memberSubscriptionService,
			MemberBalanceTransactionRepository memberBalanceTransactionRepository) {
		this.memberService = memberService;
		this.slotService = slotService;
		this.memberPlanService = memberPlanService;
		this.memberSubscriptionService = memberSubscriptionService;
		this.memberBalanceTransactionRepository = memberBalanceTransactionRepository;
	}

	@GetMapping
	public String list(MemberSearchDTO searchDTO,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			Model model) throws Exception {

		Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
		SearchResultDTO<MemberDTO> result = memberService.searchMembers(searchDTO, pageable);

		StringBuilder qs = new StringBuilder("/members?");
		if (searchDTO.getName() != null) qs.append("name=").append(searchDTO.getName()).append("&");
		if (searchDTO.getPhoneNumber() != null) qs.append("phoneNumber=").append(searchDTO.getPhoneNumber()).append("&");
		if (searchDTO.getStatus() != null) qs.append("status=").append(searchDTO.getStatus()).append("&");
		qs.append("size=").append(size).append("&page=");

		model.addAttribute("result", result);
		model.addAttribute("searchDTO", searchDTO);
		model.addAttribute("statuses", ActiveStatus.getAll());
		model.addAttribute("pageUrlPrefix", qs.toString());
		model.addAttribute("pageTitle", "Members");
		model.addAttribute("activeMenu", "members");
		return "members/list";
	}

	@GetMapping("/new")
	public String createForm(Model model) {
		model.addAttribute("memberDTO", new MemberDTO());
		loadFormRefData(model, null);
		model.addAttribute("subscription", null);
		model.addAttribute("transactions", Collections.emptyList());
		model.addAttribute("activePlans", Collections.emptyList());
		model.addAttribute("pageTitle", "New Member");
		return "members/form";
	}

	@GetMapping("/{id}/edit")
	public String editForm(@PathVariable Long id, Model model) throws Exception {
		MemberDTO dto = memberService.getMemberById(id);
		model.addAttribute("memberDTO", dto);
		loadFormRefData(model, dto.getReservedSlotId());
		model.addAttribute("activePlans", memberPlanService.getActivePlans());

		MemberSubscriptionDTO subscription = null;
		try {
			subscription = memberSubscriptionService.getActiveSubscription(id);
		} catch (NoActiveSubscriptionException e) {
			subscription = null;
		}
		model.addAttribute("subscription", subscription);

		if (subscription != null) {
			model.addAttribute("transactions", memberBalanceTransactionRepository
					.findBySubscriptionIdOrderByCreatedTimeDesc(subscription.getId()).stream()
					.map(MemberBalanceTransactionDTO::new).collect(Collectors.toList()));
		} else {
			model.addAttribute("transactions", Collections.emptyList());
		}

		model.addAttribute("pageTitle", "Edit Member");
		return "members/form";
	}

	private void loadFormRefData(Model model, Long currentSlotId) {
		model.addAttribute("statuses", ActiveStatus.getAll());
		model.addAttribute("membershipTypes", MembershipType.getAll());
		java.util.List<org.flexitech.projects.icpms.dto.slot.ParkingSlotDTO> slots = slotService.findAvailableSlots(null);
		if (currentSlotId != null && slots.stream().noneMatch(s -> s.getId().equals(currentSlotId))) {
			try {
				slots.add(slotService.getSlotById(currentSlotId));
			} catch (Exception ignored) {
			}
		}
		model.addAttribute("slots", slots);
		model.addAttribute("activeMenu", "members");
	}

	@PostMapping("/save")
	public String save(@Valid @ModelAttribute("memberDTO") MemberDTO memberDTO, org.springframework.validation.BindingResult bindingResult,
			Model model, RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			loadFormRefData(model, memberDTO.getReservedSlotId());
			model.addAttribute("subscription", null);
			model.addAttribute("transactions", Collections.emptyList());
			model.addAttribute("activePlans", Collections.emptyList());
			model.addAttribute("pageTitle", memberDTO.getId() != null ? "Edit Member" : "New Member");
			return "members/form";
		}
		try {
			memberService.manageMember(memberDTO, null);
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_SUCCESS_MESSAGE, "Member saved successfully.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_ERROR_MESSAGE, "Error saving member: " + e.getMessage());
		}
		return "redirect:/members";
	}

	@PostMapping("/{id}/delete")
	public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		try {
			memberService.deleteMember(id);
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_SUCCESS_MESSAGE, "Member deleted successfully.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_ERROR_MESSAGE, "Error deleting member: " + e.getMessage());
		}
		return "redirect:/members";
	}

	@PostMapping("/{id}/subscribe")
	public String subscribe(@PathVariable Long id, @RequestParam Long planId, RedirectAttributes redirectAttributes) {
		try {
			memberSubscriptionService.subscribe(new SubscribeMemberPlanRequest(id, planId, null));
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_SUCCESS_MESSAGE, "Member subscribed successfully.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_ERROR_MESSAGE, "Error subscribing member: " + e.getMessage());
		}
		return "redirect:/members/" + id + "/edit";
	}

	@PostMapping("/{id}/subscriptions/{subscriptionId}/renew")
	public String renew(@PathVariable Long id, @PathVariable Long subscriptionId, RedirectAttributes redirectAttributes) {
		try {
			memberSubscriptionService.renew(subscriptionId, null);
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_SUCCESS_MESSAGE, "Subscription renewed successfully.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_ERROR_MESSAGE, "Error renewing subscription: " + e.getMessage());
		}
		return "redirect:/members/" + id + "/edit";
	}

	@PostMapping("/{id}/subscriptions/{subscriptionId}/topup")
	public String topUp(@PathVariable Long id, @PathVariable Long subscriptionId, @RequestParam BigDecimal amount,
			@RequestParam(required = false) String remark, RedirectAttributes redirectAttributes) {
		try {
			memberSubscriptionService.topUp(subscriptionId, new TopUpBalanceRequest(amount, remark, null));
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_SUCCESS_MESSAGE, "Balance topped up successfully.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_ERROR_MESSAGE, "Error topping up balance: " + e.getMessage());
		}
		return "redirect:/members/" + id + "/edit";
	}

	@PostMapping("/{id}/subscriptions/{subscriptionId}/cancel")
	public String cancel(@PathVariable Long id, @PathVariable Long subscriptionId, RedirectAttributes redirectAttributes) {
		try {
			memberSubscriptionService.cancel(subscriptionId, null);
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_SUCCESS_MESSAGE, "Subscription cancelled successfully.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_ERROR_MESSAGE, "Error cancelling subscription: " + e.getMessage());
		}
		return "redirect:/members/" + id + "/edit";
	}
}