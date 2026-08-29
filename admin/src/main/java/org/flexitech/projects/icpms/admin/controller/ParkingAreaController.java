package org.flexitech.projects.icpms.admin.controller;

import org.flexitech.projects.icpms.common.CommonConstants;
import org.flexitech.projects.icpms.common.enums.ActiveStatus;
import org.flexitech.projects.icpms.dto.SearchResultDTO;
import org.flexitech.projects.icpms.dto.parking.ParkingAreaDTO;
import org.flexitech.projects.icpms.dto.parking.ParkingAreaSearchDTO;
import org.flexitech.projects.icpms.service.gate.GateService;
import org.flexitech.projects.icpms.service.parking.ParkingAreaService;
import org.flexitech.projects.icpms.service.tariff.TariffService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/parking-areas")
public class ParkingAreaController {

	private final ParkingAreaService parkingAreaService;
	private final GateService gateService;
	private final TariffService tariffService;

	public ParkingAreaController(ParkingAreaService parkingAreaService, GateService gateService, TariffService tariffService) {
		this.parkingAreaService = parkingAreaService;
		this.gateService = gateService;
		this.tariffService = tariffService;
	}

	@GetMapping
	public String list(ParkingAreaSearchDTO searchDTO,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			Model model) throws Exception {

		Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
		SearchResultDTO<ParkingAreaDTO> result = parkingAreaService.searchParkingAreas(searchDTO, pageable);

		StringBuilder qs = new StringBuilder("/parking-areas?");
		if (searchDTO.getName() != null) qs.append("name=").append(searchDTO.getName()).append("&");
		if (searchDTO.getGateId() != null) qs.append("gateId=").append(searchDTO.getGateId()).append("&");
		if (searchDTO.getStatus() != null) qs.append("status=").append(searchDTO.getStatus()).append("&");
		qs.append("size=").append(size).append("&page=");

		model.addAttribute("result", result);
		model.addAttribute("searchDTO", searchDTO);
		model.addAttribute("statuses", ActiveStatus.getAll());
		model.addAttribute("gates", gateService.findAllActiveGates());
		model.addAttribute("pageUrlPrefix", qs.toString());
		model.addAttribute("pageTitle", "Parking Areas");
		model.addAttribute("activeMenu", "parking-areas");
		return "parking-areas/list";
	}

	@GetMapping("/new")
	public String createForm(Model model) {
		model.addAttribute("parkingAreaDTO", new ParkingAreaDTO());
		loadFormRefData(model);
		model.addAttribute("pageTitle", "New Parking Area");
		return "parking-areas/form";
	}

	@GetMapping("/{id}/edit")
	public String editForm(@PathVariable Long id, Model model) throws Exception {
		model.addAttribute("parkingAreaDTO", parkingAreaService.getParkingAreaById(id));
		loadFormRefData(model);
		model.addAttribute("pageTitle", "Edit Parking Area");
		return "parking-areas/form";
	}

	private void loadFormRefData(Model model) {
		model.addAttribute("statuses", ActiveStatus.getAll());
		/* model.addAttribute("gates", gateService.findAllActiveGates()); */
		model.addAttribute("tariffList", this.tariffService.findAllActiveTariffs());

		model.addAttribute("activeMenu", "parking-areas");
	}

	@PostMapping("/save")
	public String save(@Valid @ModelAttribute("parkingAreaDTO") ParkingAreaDTO parkingAreaDTO,
			org.springframework.validation.BindingResult bindingResult,
			Model model, RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			loadFormRefData(model);
			model.addAttribute("pageTitle", parkingAreaDTO.getId() != null ? "Edit Parking Area" : "New Parking Area");
			return "parking-areas/form";
		}
		try {
			parkingAreaService.manageParkingArea(parkingAreaDTO);
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_SUCCESS_MESSAGE, "Parking area saved successfully.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_ERROR_MESSAGE, "Error saving parking area: " + e.getMessage());
		}
		return "redirect:/parking-areas";
	}

	@PostMapping("/{id}/delete")
	public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		try {
			parkingAreaService.deleteParkingArea(id);
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_SUCCESS_MESSAGE, "Parking area deleted successfully.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_ERROR_MESSAGE, "Error deleting parking area: " + e.getMessage());
		}
		return "redirect:/parking-areas";
	}
}