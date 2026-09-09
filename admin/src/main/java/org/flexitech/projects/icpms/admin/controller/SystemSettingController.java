package org.flexitech.projects.icpms.admin.controller;

import java.util.Collections;
import java.util.List;

import org.flexitech.projects.icpms.common.enums.InputType;
import org.flexitech.projects.icpms.common.exceptions.SystemSettingNotFoundException;
import org.flexitech.projects.icpms.dto.system_setting.SystemSettingDTO;
import org.flexitech.projects.icpms.dto.system_setting.SystemSettingManageDTO;
import org.flexitech.projects.icpms.dto.system_setting.SystemSettingSearchDTO;
import org.flexitech.projects.icpms.service.setting.SystemSettingService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/settings")
@Slf4j
@RequiredArgsConstructor
public class SystemSettingController {

	private final SystemSettingService systemSettingService;

	@GetMapping
	public String settingPage(Model model) {

		SystemSettingManageDTO dto = new SystemSettingManageDTO();
		SystemSettingSearchDTO search = new SystemSettingSearchDTO();
		search.setPageNo(1);
		search.setLimit(100);
		dto.setSettings(systemSettingService.searchSystemSetting(search).getResults());
		model.addAttribute("inputTypeList", InputType.getAll());
		model.addAttribute("dto", dto);
		return "setting/manage";
	}

	@PostMapping("/batch-update")
	@ResponseBody
	public ResponseEntity<?> batchUpdateSettings(@RequestBody List<SystemSettingDTO> settings) {
	    try {
	        List<SystemSettingDTO> updated = systemSettingService.batchUpdateSystemSettings(settings);
	        return ResponseEntity.ok(Collections.singletonMap("updated", updated.size()));
	    } catch (SystemSettingNotFoundException e) {
	        return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
	    } catch (Exception e) {
	        log.error("Batch update failed", e);
	        return ResponseEntity.status(500).body(Collections.singletonMap("message", "Internal server error"));
	    }
	}
}