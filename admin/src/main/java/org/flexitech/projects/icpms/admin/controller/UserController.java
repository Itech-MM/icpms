package org.flexitech.projects.icpms.admin.controller;

import org.flexitech.projects.icpms.common.CommonConstants;
import org.flexitech.projects.icpms.common.enums.ActiveStatus;
import org.flexitech.projects.icpms.dto.SearchResultDTO;
import org.flexitech.projects.icpms.dto.user.UserDTO;
import org.flexitech.projects.icpms.dto.user.UserSearchDTO;
import org.flexitech.projects.icpms.service.role.RoleService;
import org.flexitech.projects.icpms.service.user.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/users")
public class UserController {

	private final UserService userService;
	private final RoleService roleService;

	public UserController(UserService userService, RoleService roleService) {
		this.userService = userService;
		this.roleService = roleService;
	}

	@GetMapping
	public String list(UserSearchDTO searchDTO,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			Model model) throws Exception {

		Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
		SearchResultDTO<UserDTO> result = userService.searchUsers(searchDTO, pageable);

		StringBuilder qs = new StringBuilder("/users?");
		if (searchDTO.getName() != null) qs.append("name=").append(searchDTO.getName()).append("&");
		if (searchDTO.getPhoneNumber() != null) qs.append("phoneNumber=").append(searchDTO.getPhoneNumber()).append("&");
		qs.append("size=").append(size).append("&page=");

		model.addAttribute("result", result);
		model.addAttribute("searchDTO", searchDTO);
		model.addAttribute("pageUrlPrefix", qs.toString());
		model.addAttribute("pageTitle", "Users");
		model.addAttribute("activeMenu", "users");
		return "users/list";
	}

	@GetMapping("/new")
	public String createForm(Model model) throws Exception {
		model.addAttribute("userDTO", new UserDTO());
		loadFormRefData(model);
		model.addAttribute("pageTitle", "New User");
		return "users/form";
	}

	@GetMapping("/{id}/edit")
	public String editForm(@PathVariable Long id, Model model) throws Exception {
		model.addAttribute("userDTO", userService.getUserById(id));
		loadFormRefData(model);
		model.addAttribute("pageTitle", "Edit User");
		return "users/form";
	}

	private void loadFormRefData(Model model) throws Exception {
		model.addAttribute("statuses", ActiveStatus.getAll());
		model.addAttribute("roles", roleService.findAllActiveRoles());
		model.addAttribute("activeMenu", "users");
	}

	@PostMapping("/save")
	public String save(@Valid @ModelAttribute("userDTO") UserDTO userDTO, org.springframework.validation.BindingResult bindingResult,
			Model model, RedirectAttributes redirectAttributes) throws Exception {
		if (bindingResult.hasErrors()) {
			loadFormRefData(model);
			model.addAttribute("pageTitle", userDTO.getId() != null ? "Edit User" : "New User");
			return "users/form";
		}
		try {
			userService.manageUser(userDTO);
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_SUCCESS_MESSAGE, "User saved successfully.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_ERROR_MESSAGE, "Error saving user: " + e.getMessage());
		}
		return "redirect:/users";
	}

	@PostMapping("/{id}/delete")
	public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		try {
			userService.deleteUser(id);
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_SUCCESS_MESSAGE, "User deleted successfully.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_ERROR_MESSAGE, "Error deleting user: " + e.getMessage());
		}
		return "redirect:/users";
	}
}
