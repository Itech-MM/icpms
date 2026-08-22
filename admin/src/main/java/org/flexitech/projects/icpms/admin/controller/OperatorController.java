package org.flexitech.projects.icpms.admin.controller;

import org.flexitech.projects.icpms.common.CommonConstants;
import org.flexitech.projects.icpms.common.enums.ActiveStatus;
import org.flexitech.projects.icpms.common.enums.OperatorRole;
import org.flexitech.projects.icpms.dto.SearchResultDTO;
import org.flexitech.projects.icpms.dto.operator.OperatorDTO;
import org.flexitech.projects.icpms.dto.operator.OperatorSearchDTO;
import org.flexitech.projects.icpms.service.operator.OperatorService;
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
@RequestMapping("/operators")
public class OperatorController {

	private final OperatorService operatorService;
	private final SiteService siteService;

	public OperatorController(OperatorService operatorService, SiteService siteService) {
		this.operatorService = operatorService;
		this.siteService = siteService;
	}

	@GetMapping
	public String list(OperatorSearchDTO searchDTO,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			Model model) throws Exception {

		Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
		SearchResultDTO<OperatorDTO> result = operatorService.searchOperators(searchDTO, pageable);

		StringBuilder qs = new StringBuilder("/operators?");
		if (searchDTO.getName() != null) qs.append("name=").append(searchDTO.getName()).append("&");
		if (searchDTO.getUsername() != null) qs.append("username=").append(searchDTO.getUsername()).append("&");
		if (searchDTO.getSiteId() != null) qs.append("siteId=").append(searchDTO.getSiteId()).append("&");
		if (searchDTO.getStatus() != null) qs.append("status=").append(searchDTO.getStatus()).append("&");
		qs.append("size=").append(size).append("&page=");

		model.addAttribute("result", result);
		model.addAttribute("searchDTO", searchDTO);
		model.addAttribute("statuses", ActiveStatus.getAll());
		model.addAttribute("sites", siteService.findAllActiveSites());
		model.addAttribute("pageUrlPrefix", qs.toString());
		model.addAttribute("pageTitle", "Operators");
		model.addAttribute("activeMenu", "operators");
		return "operators/list";
	}

	@GetMapping("/new")
	public String createForm(Model model) {
		model.addAttribute("operatorDTO", new OperatorDTO());
		loadFormRefData(model);
		model.addAttribute("pageTitle", "New Operator");
		return "operators/form";
	}

	@GetMapping("/{id}/edit")
	public String editForm(@PathVariable Long id, Model model) throws Exception {
		model.addAttribute("operatorDTO", operatorService.getOperatorById(id));
		loadFormRefData(model);
		model.addAttribute("pageTitle", "Edit Operator");
		return "operators/form";
	}

	private void loadFormRefData(Model model) {
		model.addAttribute("statuses", ActiveStatus.getAll());
		model.addAttribute("operatorRoles", OperatorRole.getAll());
		model.addAttribute("sites", siteService.findAllActiveSites());
		model.addAttribute("activeMenu", "operators");
	}

	@PostMapping("/save")
	public String save(@Valid @ModelAttribute("operatorDTO") OperatorDTO operatorDTO, org.springframework.validation.BindingResult bindingResult,
			Model model, RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			loadFormRefData(model);
			model.addAttribute("pageTitle", operatorDTO.getId() != null ? "Edit Operator" : "New Operator");
			return "operators/form";
		}
		try {
			operatorService.manageOperator(operatorDTO);
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_SUCCESS_MESSAGE, "Operator saved successfully.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_ERROR_MESSAGE, "Error saving operator: " + e.getMessage());
		}
		return "redirect:/operators";
	}

	@PostMapping("/{id}/delete")
	public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		try {
			operatorService.deleteOperator(id);
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_SUCCESS_MESSAGE, "Operator deleted successfully.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_ERROR_MESSAGE, "Error deleting operator: " + e.getMessage());
		}
		return "redirect:/operators";
	}
}
