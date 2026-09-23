package org.flexitech.projects.icpms.admin.controller;

import org.flexitech.projects.icpms.common.CommonConstants;
import org.flexitech.projects.icpms.common.MenuCodeConstants;
import org.flexitech.projects.icpms.common.enums.ActiveStatus;
import org.flexitech.projects.icpms.dto.SearchResultDTO;
import org.flexitech.projects.icpms.dto.site.SiteDTO;
import org.flexitech.projects.icpms.dto.site.SiteSearchDTO;
import org.flexitech.projects.icpms.service.site.SiteService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/sites")
public class SiteController {

	private final SiteService siteService;

	public SiteController(SiteService siteService) {
		this.siteService = siteService;
	}

	@GetMapping
	@PreAuthorize("@menuSecurity.hasMenuAccess('" + MenuCodeConstants.MENU_SITES
			+ "') or @menuSecurity.hasMenuView('" + MenuCodeConstants.MENU_SITES + "')")
	public String list(SiteSearchDTO searchDTO,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			Model model) throws Exception {

		Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
		SearchResultDTO<SiteDTO> result = siteService.searchSites(searchDTO, pageable);

		StringBuilder qs = new StringBuilder("/sites?");
		if (searchDTO.getName() != null) qs.append("name=").append(searchDTO.getName()).append("&");
		if (searchDTO.getStatus() != null) qs.append("status=").append(searchDTO.getStatus()).append("&");
		qs.append("size=").append(size).append("&page=");

		model.addAttribute("result", result);
		model.addAttribute("searchDTO", searchDTO);
		model.addAttribute("statuses", ActiveStatus.getAll());
		model.addAttribute("pageUrlPrefix", qs.toString());
		model.addAttribute("pageTitle", "Sites");
		model.addAttribute("activeMenu", "sites");
		return "sites/list";
	}

	@GetMapping("/new")
	@PreAuthorize("@menuSecurity.hasMenuEdit('" + MenuCodeConstants.MENU_SITES + "')")
	public String createForm(Model model) {
		model.addAttribute("siteDTO", new SiteDTO());
		model.addAttribute("statuses", ActiveStatus.getAll());
		model.addAttribute("pageTitle", "New Site");
		model.addAttribute("activeMenu", "sites");
		return "sites/form";
	}

	@GetMapping("/{id}/edit")
	@PreAuthorize("@menuSecurity.hasMenuEdit('" + MenuCodeConstants.MENU_SITES + "')")
	public String editForm(@PathVariable Long id, Model model) throws Exception {
		model.addAttribute("siteDTO", siteService.getSiteById(id));
		model.addAttribute("statuses", ActiveStatus.getAll());
		model.addAttribute("pageTitle", "Edit Site");
		model.addAttribute("activeMenu", "sites");
		return "sites/form";
	}

	@PostMapping("/save")
	@PreAuthorize("@menuSecurity.hasMenuEdit('" + MenuCodeConstants.MENU_SITES + "')")
	public String save(@Valid @ModelAttribute("siteDTO") SiteDTO siteDTO, org.springframework.validation.BindingResult bindingResult,
			Model model, RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("statuses", ActiveStatus.getAll());
			model.addAttribute("pageTitle", siteDTO.getId() != null ? "Edit Site" : "New Site");
			model.addAttribute("activeMenu", "sites");
			return "sites/form";
		}
		try {
			siteService.manageSite(siteDTO);
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_SUCCESS_MESSAGE, "Site saved successfully.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_ERROR_MESSAGE, "Error saving site: " + e.getMessage());
		}
		return "redirect:/sites";
	}

	@PostMapping("/{id}/delete")
	@PreAuthorize("@menuSecurity.hasMenuDelete('" + MenuCodeConstants.MENU_SITES + "')")
	public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		try {
			siteService.deleteSite(id);
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_SUCCESS_MESSAGE, "Site deleted successfully.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_ERROR_MESSAGE, "Error deleting site: " + e.getMessage());
		}
		return "redirect:/sites";
	}
}