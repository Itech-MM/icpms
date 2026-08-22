package org.flexitech.projects.icpms.admin.controller;

import org.flexitech.projects.icpms.common.CommonConstants;
import org.flexitech.projects.icpms.common.enums.SlotStatus;
import org.flexitech.projects.icpms.dto.SearchResultDTO;
import org.flexitech.projects.icpms.dto.slot.ParkingSlotBulkDTO;
import org.flexitech.projects.icpms.dto.slot.ParkingSlotDTO;
import org.flexitech.projects.icpms.dto.slot.ParkingSlotSearchDTO;
import org.flexitech.projects.icpms.service.site.SiteService;
import org.flexitech.projects.icpms.service.slot.ParkingSlotService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/parking-slots")
public class ParkingSlotController {

	private final ParkingSlotService slotService;
	private final SiteService siteService;

	public ParkingSlotController(ParkingSlotService slotService, SiteService siteService) {
		this.slotService = slotService;
		this.siteService = siteService;
	}

	@GetMapping
	public String list(ParkingSlotSearchDTO searchDTO,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			Model model) throws Exception {

		Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
		SearchResultDTO<ParkingSlotDTO> result = slotService.searchSlots(searchDTO, pageable);

		StringBuilder qs = new StringBuilder("/parking-slots?");
		if (searchDTO.getSlotNumber() != null) qs.append("slotNumber=").append(searchDTO.getSlotNumber()).append("&");
		if (searchDTO.getSiteId() != null) qs.append("siteId=").append(searchDTO.getSiteId()).append("&");
		if (searchDTO.getStatus() != null) qs.append("status=").append(searchDTO.getStatus()).append("&");
		qs.append("size=").append(size).append("&page=");

		model.addAttribute("result", result);
		model.addAttribute("searchDTO", searchDTO);
		model.addAttribute("slotStatuses", SlotStatus.getAll());
		model.addAttribute("sites", siteService.findAllActiveSites());
		model.addAttribute("pageUrlPrefix", qs.toString());
		model.addAttribute("pageTitle", "Parking Slots");
		model.addAttribute("activeMenu", "slots");
		return "slots/list";
	}

	

	@GetMapping("/new")
	public String createForm(Model model) {
	    model.addAttribute("slotDTO", new ParkingSlotDTO());
	    loadFormRefData(model);
	    model.addAttribute("pageTitle", "New Parking Slot");
	    return "slots/form";
	}

	private static final int MAX_BULK_RANGE = 500;

	@PostMapping("/bulk-save")
	public String bulkSave(@Valid @ModelAttribute("bulkDTO") ParkingSlotBulkDTO bulkDTO,
	        BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {

	    if (bulkDTO.getFromNumber() != null && bulkDTO.getToNumber() != null) {
	        if (bulkDTO.getFromNumber() > bulkDTO.getToNumber()) {
	            bindingResult.rejectValue("toNumber", "error.bulkDTO",
	                    "'To' must be greater than or equal to 'From'");
	        } else if ((bulkDTO.getToNumber() - bulkDTO.getFromNumber() + 1) > MAX_BULK_RANGE) {
	            bindingResult.rejectValue("toNumber", "error.bulkDTO",
	                    "Range too large - maximum " + MAX_BULK_RANGE + " slots per batch");
	        }
	    }

	    if (bindingResult.hasErrors()) {
	        model.addAttribute("slotDTO", new ParkingSlotDTO());
	        model.addAttribute("bulkDTO", bulkDTO);
	        loadFormRefData(model);
	        model.addAttribute("pageTitle", "New Parking Slot");
	        model.addAttribute("activeTab", "bulk");
	        return "slots/form";
	    }

	    try {
	        int created = slotService.bulkCreateSlots(bulkDTO);
	        redirectAttributes.addFlashAttribute(CommonConstants.FORM_SUCCESS_MESSAGE,
	                created + " parking slots created successfully.");
	    } catch (Exception e) {
	        redirectAttributes.addFlashAttribute(CommonConstants.FORM_ERROR_MESSAGE,
	                "Error creating parking slots: " + e.getMessage());
	    }
	    return "redirect:/parking-slots";
	}

	@GetMapping("/{id}/edit")
	public String editForm(@PathVariable Long id, Model model) throws Exception {
		model.addAttribute("slotDTO", slotService.getSlotById(id));
		loadFormRefData(model);
		model.addAttribute("pageTitle", "Edit Parking Slot");
		return "slots/form";
	}

	private void loadFormRefData(Model model) {
	    model.addAttribute("slotStatuses", SlotStatus.getAll());
	    model.addAttribute("sites", siteService.findAllActiveSites());
	    model.addAttribute("activeMenu", "slots");
	    if (!model.containsAttribute("bulkDTO")) {
	        model.addAttribute("bulkDTO", new ParkingSlotBulkDTO());
	    }
	}

	@PostMapping("/save")
	public String save(@Valid @ModelAttribute("slotDTO") ParkingSlotDTO slotDTO, org.springframework.validation.BindingResult bindingResult,
			Model model, RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			loadFormRefData(model);
			model.addAttribute("pageTitle", slotDTO.getId() != null ? "Edit Parking Slot" : "New Parking Slot");
			return "slots/form";
		}
		try {
			slotService.manageSlot(slotDTO);
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_SUCCESS_MESSAGE, "Parking slot saved successfully.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_ERROR_MESSAGE, "Error saving parking slot: " + e.getMessage());
		}
		return "redirect:/parking-slots";
	}

	@PostMapping("/{id}/delete")
	public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		try {
			slotService.deleteSlot(id);
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_SUCCESS_MESSAGE, "Parking slot deleted successfully.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_ERROR_MESSAGE, "Error deleting parking slot: " + e.getMessage());
		}
		return "redirect:/parking-slots";
	}
}
