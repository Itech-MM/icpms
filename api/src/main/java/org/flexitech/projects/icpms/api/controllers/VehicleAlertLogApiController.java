package org.flexitech.projects.icpms.api.controllers;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.flexitech.projects.icpms.common.CommonConstants;
import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.common.enums.ActiveStatus;
import org.flexitech.projects.icpms.dto.SearchResultDTO;
import org.flexitech.projects.icpms.dto.api.response.ApiResponse;
import org.flexitech.projects.icpms.dto.audit_logs.VehicleAlertLogDTO;
import org.flexitech.projects.icpms.dto.audit_logs.VehicleAlertLogSearchDTO;
import org.flexitech.projects.icpms.dto.gate.GateDTO;
import org.flexitech.projects.icpms.service.audit_logs.VehicleAlertLogService;
import org.flexitech.projects.icpms.service.gate.GateService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/vehicle-alerts")
@Slf4j
@RequiredArgsConstructor
public class VehicleAlertLogApiController {

	private final VehicleAlertLogService logService;
	private final GateService gateService;

	@GetMapping("/get-active-alerts")
	public ResponseEntity<ApiResponse<SearchResultDTO<VehicleAlertLogDTO>>> getMethodName(@RequestParam Integer page,
			Authentication authentication, HttpServletRequest httpRequest) {

		try {

			String gateIpAddress = httpRequest.getHeader(CommonConstants.GATE_IP_HEADER);
			GateDTO gate = this.gateService.findByIpAddress(gateIpAddress);

			if (gate == null) {
				return ApiResponse.badRequest("No active shift found - please open a shift first.");
			}

			VehicleAlertLogSearchDTO searchDTO = new VehicleAlertLogSearchDTO();
			searchDTO.setGateId(gate.getId());
			searchDTO.setStatus(ActiveStatus.ACTIVE.getCode());
			searchDTO.setPageNo(CommonValidators.validInteger(page) ? page -1 : 0);

			SearchResultDTO<VehicleAlertLogDTO> searchResult = logService.searchVehicleAlertLogs(searchDTO);

			return ApiResponse.ok(searchResult, "Get vehicle alert by gate success.");

		} catch (Exception e) {
			log.error("Error on get session alert logs:: {}", ExceptionUtils.getStackTrace(e));
			return ApiResponse.internalError(e.getMessage());
		}

	}

	@PatchMapping("/update-status/{id}/status/{status}")
	public ResponseEntity<ApiResponse<Void>> updateStatus(@PathVariable Long id, @PathVariable Integer status) {
		try {
			this.logService.updateStatus(status, id);
		} catch (Exception e) {
			log.error("Error on get session alert logs:: {}", ExceptionUtils.getStackTrace(e));
			return ApiResponse.internalError(e.getMessage());
		}
		return ApiResponse.ok(null, "Update status success.");
	}

}
