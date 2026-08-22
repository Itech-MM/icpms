package org.flexitech.projects.icpms.admin.controller;

import org.flexitech.projects.icpms.common.CommonConstants;
import org.flexitech.projects.icpms.common.enums.ActiveStatus;
import org.flexitech.projects.icpms.common.enums.MembershipType;
import org.flexitech.projects.icpms.dto.SearchResultDTO;
import org.flexitech.projects.icpms.dto.member.MemberDTO;
import org.flexitech.projects.icpms.dto.member.MemberSearchDTO;
import org.flexitech.projects.icpms.service.member.MemberService;
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

	public MemberController(MemberService memberService, ParkingSlotService slotService) {
		this.memberService = memberService;
		this.slotService = slotService;
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
		model.addAttribute("pageTitle", "New Member");
		return "members/form";
	}

	@GetMapping("/{id}/edit")
	public String editForm(@PathVariable Long id, Model model) throws Exception {
		MemberDTO dto = memberService.getMemberById(id);
		model.addAttribute("memberDTO", dto);
		loadFormRefData(model, dto.getReservedSlotId());
		model.addAttribute("pageTitle", "Edit Member");
		return "members/form";
	}

	private void loadFormRefData(Model model, Long currentSlotId) {
		model.addAttribute("statuses", ActiveStatus.getAll());
		model.addAttribute("membershipTypes", MembershipType.getAll());
		// available slots + the member's own currently reserved slot (if editing)
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
			model.addAttribute("pageTitle", memberDTO.getId() != null ? "Edit Member" : "New Member");
			return "members/form";
		}
		try {
			memberService.manageMember(memberDTO);
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
}
