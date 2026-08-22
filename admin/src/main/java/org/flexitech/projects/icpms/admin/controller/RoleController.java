package org.flexitech.projects.icpms.admin.controller;

import org.flexitech.projects.icpms.common.CommonConstants;
import org.flexitech.projects.icpms.dto.role.RoleDTO;
import org.flexitech.projects.icpms.service.role.RoleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/roles")
public class RoleController {

	private final RoleService roleService;

	public RoleController(RoleService roleService) {
		this.roleService = roleService;
	}

	@GetMapping
	public String list(Model model) throws Exception {
		model.addAttribute("roles", roleService.getAllRoles());
		model.addAttribute("pageTitle", "Roles");
		model.addAttribute("activeMenu", "roles");
		return "roles/list";
	}

	@GetMapping("/new")
	public String createForm(Model model) {
		model.addAttribute("roleDTO", new RoleDTO());
		model.addAttribute("pageTitle", "New Role");
		model.addAttribute("activeMenu", "roles");
		return "roles/form";
	}

	@GetMapping("/{id}/edit")
	public String editForm(@PathVariable Long id, Model model) throws Exception {
		model.addAttribute("roleDTO", roleService.getRoleById(id));
		model.addAttribute("pageTitle", "Edit Role");
		model.addAttribute("activeMenu", "roles");
		return "roles/form";
	}

	@PostMapping("/save")
	public String save(@Valid @ModelAttribute("roleDTO") RoleDTO roleDTO, org.springframework.validation.BindingResult bindingResult,
			Model model, RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("pageTitle", roleDTO.getId() != null ? "Edit Role" : "New Role");
			model.addAttribute("activeMenu", "roles");
			return "roles/form";
		}
		try {
			roleService.manageRole(roleDTO);
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_SUCCESS_MESSAGE, "Role saved successfully.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_ERROR_MESSAGE, "Error saving role: " + e.getMessage());
		}
		return "redirect:/roles";
	}

	@PostMapping("/{id}/delete")
	public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		try {
			roleService.deleteRole(id);
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_SUCCESS_MESSAGE, "Role deleted successfully.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_ERROR_MESSAGE, "Error deleting role: " + e.getMessage());
		}
		return "redirect:/roles";
	}
}
