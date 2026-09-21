package org.flexitech.projects.icpms.api.controllers;

import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.flexitech.projects.icpms.common.CommonConstants;
import org.flexitech.projects.icpms.dto.api.request.gate_device.GateDeviceBatchStatusRequest;
import org.flexitech.projects.icpms.dto.api.response.ApiResponse;
import org.flexitech.projects.icpms.dto.api.response.gate_device.GateDeviceBatchStatusResponse;
import org.flexitech.projects.icpms.dto.gate.GateDTO;
import org.flexitech.projects.icpms.dto.gate.GateDeviceDTO;
import org.flexitech.projects.icpms.service.gate.GateDeviceService;
import org.flexitech.projects.icpms.service.gate.GateService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/gate/devices")
@RequiredArgsConstructor
@Slf4j
public class GateDeviceApiController {

	private final GateDeviceService gateDeviceService;
	private final GateService gateService;
	
	@GetMapping
	public ResponseEntity<ApiResponse<List<GateDeviceDTO>>> getGateDevices(Authentication authentication, HttpServletRequest req) {
		try {
			String gateIpAddress = req.getHeader(CommonConstants.GATE_IP_HEADER);
			GateDTO gate = this.gateService.findByIpAddress(gateIpAddress);

			if (gate == null) {
				return ApiResponse.badRequest("No active shift found - please open a shift first.");
			}

			return ApiResponse.ok(gateDeviceService.getDevicesByGate(gate.getId()), "Getting device data success.");
			
		}catch (Exception e) {
			log.error("Error on getting gate devices:: {}", ExceptionUtils.getStackTrace(e));
			return ApiResponse.internalError(e.getMessage());
		}
	}
	
	@PutMapping("/batch-status")
	public ResponseEntity<ApiResponse<GateDeviceBatchStatusResponse>> updateGateDevicesStatusBatch(
	        @Valid @RequestBody GateDeviceBatchStatusRequest request,
	        HttpServletRequest req) {
	    try {
	        String gateIpAddress = req.getHeader(CommonConstants.GATE_IP_HEADER);
	        GateDTO gate = this.gateService.findByIpAddress(gateIpAddress);

	        if (gate == null) {
	            return ApiResponse.badRequest("No active shift found - please open a shift first.");
	        }

	        GateDeviceBatchStatusResponse result =
	                gateDeviceService.updateGateDeviceStatusBatch(request);

	        return ApiResponse.ok(result, "Device statuses updated.");
	    } catch (Exception e) {
	        log.error("Error on batch updating gate devices:: {}",
	                ExceptionUtils.getStackTrace(e));
	        return ApiResponse.internalError(e.getMessage());
	    }
	}
	
	
}
