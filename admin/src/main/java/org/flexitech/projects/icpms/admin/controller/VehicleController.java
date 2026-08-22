package org.flexitech.projects.icpms.admin.controller;

import org.flexitech.projects.icpms.common.CommonConstants;
import org.flexitech.projects.icpms.common.enums.ActiveStatus;
import org.flexitech.projects.icpms.dto.SearchResultDTO;
import org.flexitech.projects.icpms.dto.vehicle.VehicleDTO;
import org.flexitech.projects.icpms.dto.vehicle.VehicleSearchDTO;
import org.flexitech.projects.icpms.service.member.MemberService;
import org.flexitech.projects.icpms.service.vehicle.VehicleService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/vehicles")
public class VehicleController {

	private final VehicleService vehicleService;
	private final MemberService memberService;

	public VehicleController(VehicleService vehicleService, MemberService memberService) {
		this.vehicleService = vehicleService;
		this.memberService = memberService;
	}

	@GetMapping
	public String list(VehicleSearchDTO searchDTO,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			Model model) throws Exception {

		Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
		SearchResultDTO<VehicleDTO> result = vehicleService.searchVehicles(searchDTO, pageable);

		StringBuilder qs = new StringBuilder("/vehicles?");
		if (searchDTO.getPlateNumber() != null) qs.append("plateNumber=").append(searchDTO.getPlateNumber()).append("&");
		if (searchDTO.getStatus() != null) qs.append("status=").append(searchDTO.getStatus()).append("&");
		qs.append("size=").append(size).append("&page=");

		model.addAttribute("result", result);
		model.addAttribute("searchDTO", searchDTO);
		model.addAttribute("statuses", ActiveStatus.getAll());
		model.addAttribute("pageUrlPrefix", qs.toString());
		model.addAttribute("pageTitle", "Vehicles");
		model.addAttribute("activeMenu", "vehicles");
		return "vehicles/list";
	}

	@GetMapping("/new")
	public String createForm(Model model) {
		model.addAttribute("vehicleDTO", new VehicleDTO());
		loadFormRefData(model);
		model.addAttribute("pageTitle", "New Vehicle");
		return "vehicles/form";
	}

	@GetMapping("/{id}/edit")
	public String editForm(@PathVariable Long id, Model model) throws Exception {
		model.addAttribute("vehicleDTO", vehicleService.getVehicleById(id));
		loadFormRefData(model);
		model.addAttribute("pageTitle", "Edit Vehicle");
		return "vehicles/form";
	}

	private void loadFormRefData(Model model) {
		model.addAttribute("statuses", ActiveStatus.getAll());
		model.addAttribute("members", memberService.findAllActiveMembers());
		model.addAttribute("activeMenu", "vehicles");
	}

	@PostMapping("/save")
	public String save(@Valid @ModelAttribute("vehicleDTO") VehicleDTO vehicleDTO, org.springframework.validation.BindingResult bindingResult,
			Model model, RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			loadFormRefData(model);
			model.addAttribute("pageTitle", vehicleDTO.getId() != null ? "Edit Vehicle" : "New Vehicle");
			return "vehicles/form";
		}
		try {
			vehicleService.manageVehicle(vehicleDTO);
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_SUCCESS_MESSAGE, "Vehicle saved successfully.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_ERROR_MESSAGE, "Error saving vehicle: " + e.getMessage());
		}
		return "redirect:/vehicles";
	}

	@PostMapping("/{id}/delete")
	public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		try {
			vehicleService.deleteVehicle(id);
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_SUCCESS_MESSAGE, "Vehicle deleted successfully.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_ERROR_MESSAGE, "Error deleting vehicle: " + e.getMessage());
		}
		return "redirect:/vehicles";
	}
}
