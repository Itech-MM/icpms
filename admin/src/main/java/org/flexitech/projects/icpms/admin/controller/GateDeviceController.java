package org.flexitech.projects.icpms.admin.controller;

import org.flexitech.projects.icpms.common.CommonConstants;
import org.flexitech.projects.icpms.common.enums.ActiveStatus;
import org.flexitech.projects.icpms.common.enums.GateDeviceType;
import org.flexitech.projects.icpms.common.enums.GateType;
import org.flexitech.projects.icpms.dto.gate.GateDeviceDTO;
import org.flexitech.projects.icpms.service.gate.GateDeviceService;
import org.flexitech.projects.icpms.service.gate.GateService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/gates/{gateId}/devices")
public class GateDeviceController {

	private final GateDeviceService gateDeviceService;
	private final GateService gateService;

	public GateDeviceController(GateDeviceService gateDeviceService, GateService gateService) {
		this.gateDeviceService = gateDeviceService;
		this.gateService = gateService;
	}

	@GetMapping
	public String list(@PathVariable Long gateId,
			@RequestParam(required = false) Long editId,
			Model model) throws Exception {

		model.addAttribute("gate", gateService.getGateById(gateId));
		model.addAttribute("devices", gateDeviceService.getDevicesByGate(gateId));

		GateDeviceDTO formDTO;
		if (editId != null) {
			formDTO = gateDeviceService.getDeviceById(editId);
		} else {
			formDTO = new GateDeviceDTO();
			formDTO.setGateId(gateId);
			formDTO.setStatus(ActiveStatus.ACTIVE.getCode());
		}
		model.addAttribute("deviceDTO", formDTO);

		model.addAttribute("deviceTypes", GateDeviceType.getAll());
		model.addAttribute("directions", GateType.getAll());
		model.addAttribute("statuses", ActiveStatus.getAll());
		model.addAttribute("pageTitle", "Gate Devices");
		model.addAttribute("activeMenu", "gates");
		return "gates/devices";
	}

	@PostMapping("/save")
	public String save(@PathVariable Long gateId, @ModelAttribute GateDeviceDTO deviceDTO,
			RedirectAttributes redirectAttributes) {
		try {
			deviceDTO.setGateId(gateId);
			gateDeviceService.manageDevice(deviceDTO);
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_SUCCESS_MESSAGE, "Device saved successfully.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_ERROR_MESSAGE, "Error saving device: " + e.getMessage());
		}
		return "redirect:/gates/" + gateId + "/devices";
	}

	@PostMapping("/{deviceId}/delete")
	public String delete(@PathVariable Long gateId, @PathVariable Long deviceId, RedirectAttributes redirectAttributes) {
		try {
			gateDeviceService.deleteDevice(deviceId);
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_SUCCESS_MESSAGE, "Device deleted successfully.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute(CommonConstants.FORM_ERROR_MESSAGE, "Error deleting device: " + e.getMessage());
		}
		return "redirect:/gates/" + gateId + "/devices";
	}
}