package org.flexitech.projects.icpms.admin.controller;

import org.flexitech.projects.icpms.common.CommonConstants;
import org.flexitech.projects.icpms.common.enums.ActiveStatus;
import org.flexitech.projects.icpms.dto.SearchResultDTO;
import org.flexitech.projects.icpms.dto.tariff.TariffDTO;
import org.flexitech.projects.icpms.dto.tariff.TariffRateDTO;
import org.flexitech.projects.icpms.dto.tariff.TariffSearchDTO;
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
@RequestMapping("/tariffs")
public class TariffController {

	private final TariffService tariffService;

	public TariffController(TariffService tariffService) {
		this.tariffService = tariffService;
	}

	@GetMapping
	public String list(TariffSearchDTO searchDTO,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			Model model) throws Exception {

		Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
		SearchResultDTO<TariffDTO> result = tariffService.searchTariffs(searchDTO, pageable);

		StringBuilder qs = new StringBuilder("/tariffs?");
		if (searchDTO.getName() != null) qs.append("name=").append(searchDTO.getName()).append("&");
		if (searchDTO.getStatus() != null) qs.append("status=").append(searchDTO.getStatus()).append("&");
		qs.append("size=").append(size).append("&page=");

		model.addAttribute("result", result);
		model.addAttribute("searchDTO", searchDTO);
		model.addAttribute("statuses", ActiveStatus.getAll());
		model.addAttribute("pageUrlPrefix", qs.toString());
		model.addAttribute("pageTitle", "Tariffs");
		model.addAttribute("activeMenu", "tariffs");
		return "tariffs/list";
	}

	@GetMapping("/new")
	public String createForm(Model model) {
		model.addAttribute("tariffDTO", new TariffDTO());
		model.addAttribute("statuses", ActiveStatus.getAll());
		model.addAttribute("pageTitle", "New Tariff");
		model.addAttribute("activeMenu", "tariffs");
		return "tariffs/form";
	}

	@GetMapping("/{id}/edit")
	public String editForm(@PathVariable Long id, Model model) throws Exception {
		model.addAttribute("tariffDTO", tariffService.getTariffById(id));
		model.addAttribute("statuses", ActiveStatus.getAll());
		model.addAttribute("pageTitle", "Edit Tariff");
		model.addAttribute("activeMenu", "tariffs");
		return "tariffs/form";
	}

	@PostMapping("/save")
	public String save(@Valid @ModelAttribute("tariffDTO") TariffDTO tariffDTO, org.springframework.validation.BindingResult bindingResult,
			Model model, RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("statuses", ActiveStatus.getAll());
			model.addAttribute("pageTitle", tariffDTO.getId() != null ? "Edit Tariff" : "New Tariff");
			model.addAttribute("activeMenu", "tariffs");
			return "tariffs/form";
		}
		try {
			TariffDTO saved = tariffService.manageTariff(tariffDTO);
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_SUCCESS_MESSAGE, "Tariff saved successfully.");
			if (tariffDTO.getId() == null) {
				return "redirect:/tariffs/" + saved.getId() + "/rates";
			}
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_ERROR_MESSAGE, "Error saving tariff: " + e.getMessage());
		}
		return "redirect:/tariffs";
	}

	@PostMapping("/{id}/delete")
	public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		try {
			tariffService.deleteTariff(id);
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_SUCCESS_MESSAGE, "Tariff deleted successfully.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_ERROR_MESSAGE, "Error deleting tariff: " + e.getMessage());
		}
		return "redirect:/tariffs";
	}

	@GetMapping("/{id}/rates")
	public String rates(@PathVariable Long id, Model model) throws Exception {
		model.addAttribute("tariffDTO", tariffService.getTariffById(id));
		model.addAttribute("newRate", new TariffRateDTO());
		model.addAttribute("pageTitle", "Manage Tariff Rates");
		model.addAttribute("activeMenu", "tariffs");
		return "tariffs/rates";
	}

	@PostMapping("/{id}/rates/save")
	public String saveRate(@PathVariable Long id, @ModelAttribute TariffRateDTO rateDTO, RedirectAttributes redirectAttributes) {
		try {
			rateDTO.setTariffId(id);
			tariffService.addRate(rateDTO);
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_SUCCESS_MESSAGE, "Tariff rate saved successfully.");
		} catch (Exception e) {
			e.printStackTrace(); 
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_ERROR_MESSAGE, "Error saving rate: " + e.getMessage());
		}
		return "redirect:/tariffs/" + id + "/rates";
	}

	@PostMapping("/{id}/rates/{rateId}/delete")
	public String deleteRate(@PathVariable Long id, @PathVariable Long rateId, RedirectAttributes redirectAttributes) {
		try {
			tariffService.deleteRate(rateId);
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_SUCCESS_MESSAGE, "Tariff rate deleted successfully.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_ERROR_MESSAGE, "Error deleting rate: " + e.getMessage());
		}
		return "redirect:/tariffs/" + id + "/rates";
	}
}
