package org.flexitech.projects.icpms.api.controllers;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.flexitech.projects.icpms.common.CommonConstants;
import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.dto.api.response.ApiResponse;
import org.flexitech.projects.icpms.dto.parking.ParkingAreaRealtimeDTO;
import org.flexitech.projects.icpms.service.parking.ParkingAreaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/parking-area")
@Slf4j
public class ParkingAreaApiController {

	private final ParkingAreaService parkingAreaService;

	public ParkingAreaApiController(ParkingAreaService parkingAreaService) {
		this.parkingAreaService = parkingAreaService;
	}

	@GetMapping("/realtime")
	public ResponseEntity<ApiResponse<ParkingAreaRealtimeDTO>> getRealtimeParkingArea(HttpServletRequest httpRequest) {
		try {
			String gateIpAddress = httpRequest.getHeader(CommonConstants.GATE_IP_HEADER);
			if (!CommonValidators.validString(gateIpAddress)) {
				return ApiResponse.badRequest("Missing gate identifier.");
			}
			ParkingAreaRealtimeDTO data = this.parkingAreaService.getRealtimeByGate(gateIpAddress);
			return ApiResponse.ok(data, "Get realtime parking area data success!");
		} catch (Exception e) {
			log.error("Error on getting realtime parking area:: {}", ExceptionUtils.getStackTrace(e));
			return ApiResponse.internalError(e.getMessage());
		}
	}
}