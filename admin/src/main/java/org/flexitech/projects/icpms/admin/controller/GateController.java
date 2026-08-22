package org.flexitech.projects.icpms.admin.controller;

import org.flexitech.projects.icpms.common.CommonConstants;
import org.flexitech.projects.icpms.common.enums.ActiveStatus;
import org.flexitech.projects.icpms.common.enums.GateType;
import org.flexitech.projects.icpms.dto.SearchResultDTO;
import org.flexitech.projects.icpms.dto.gate.GateDTO;
import org.flexitech.projects.icpms.dto.gate.GateSearchDTO;
import org.flexitech.projects.icpms.service.gate.GateService;
import org.flexitech.projects.icpms.service.site.SiteService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/gates")
public class GateController {

	private final GateService gateService;
	private final SiteService siteService;

	public GateController(GateService gateService, SiteService siteService) {
		this.gateService = gateService;
		this.siteService = siteService;
	}

	@GetMapping
	public String list(GateSearchDTO searchDTO,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			Model model) throws Exception {

		Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
		SearchResultDTO<GateDTO> result = gateService.searchGates(searchDTO, pageable);

		StringBuilder qs = new StringBuilder("/gates?");
		if (searchDTO.getName() != null) qs.append("name=").append(searchDTO.getName()).append("&");
		if (searchDTO.getSiteId() != null) qs.append("siteId=").append(searchDTO.getSiteId()).append("&");
		if (searchDTO.getStatus() != null) qs.append("status=").append(searchDTO.getStatus()).append("&");
		qs.append("size=").append(size).append("&page=");

		model.addAttribute("result", result);
		model.addAttribute("searchDTO", searchDTO);
		model.addAttribute("statuses", ActiveStatus.getAll());
		model.addAttribute("gateTypes", GateType.getAll());
		model.addAttribute("sites", siteService.findAllActiveSites());
		model.addAttribute("pageUrlPrefix", qs.toString());
		model.addAttribute("pageTitle", "Gates");
		model.addAttribute("activeMenu", "gates");
		return "gates/list";
	}

	@GetMapping("/new")
	public String createForm(Model model) {
		model.addAttribute("gateDTO", new GateDTO());
		loadFormRefData(model);
		model.addAttribute("pageTitle", "New Gate");
		return "gates/form";
	}

	@GetMapping("/{id}/edit")
	public String editForm(@PathVariable Long id, Model model) throws Exception {
		model.addAttribute("gateDTO", gateService.getGateById(id));
		loadFormRefData(model);
		model.addAttribute("pageTitle", "Edit Gate");
		return "gates/form";
	}

	private void loadFormRefData(Model model) {
		model.addAttribute("statuses", ActiveStatus.getAll());
		model.addAttribute("gateTypes", GateType.getAll());
		model.addAttribute("sites", siteService.findAllActiveSites());
		model.addAttribute("activeMenu", "gates");
	}

	@PostMapping("/save")
	public String save(@Valid @ModelAttribute("gateDTO") GateDTO gateDTO, org.springframework.validation.BindingResult bindingResult,
			Model model, RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			loadFormRefData(model);
			model.addAttribute("pageTitle", gateDTO.getId() != null ? "Edit Gate" : "New Gate");
			return "gates/form";
		}
		try {
			gateService.manageGate(gateDTO);
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_SUCCESS_MESSAGE, "Gate saved successfully.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_ERROR_MESSAGE, "Error saving gate: " + e.getMessage());
		}
		return "redirect:/gates";
	}

	@PostMapping("/{id}/delete")
	public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		try {
			gateService.deleteGate(id);
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_SUCCESS_MESSAGE, "Gate deleted successfully.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_ERROR_MESSAGE, "Error deleting gate: " + e.getMessage());
		}
		return "redirect:/gates";
	}
}
