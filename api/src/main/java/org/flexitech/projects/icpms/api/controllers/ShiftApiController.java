package org.flexitech.projects.icpms.api.controllers;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.flexitech.projects.icpms.api.security.OperatorPrincipal;
import org.flexitech.projects.icpms.common.CommonConstants;
import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.dto.api.request.shift.EndShiftRequestDTO;
import org.flexitech.projects.icpms.dto.api.request.shift.StartShiftRequestDTO;
import org.flexitech.projects.icpms.dto.api.response.ApiResponse;
import org.flexitech.projects.icpms.dto.operator.OperatorShiftDTO;
import org.flexitech.projects.icpms.dto.operator.OperatorShiftSummaryDTO;
import org.flexitech.projects.icpms.service.operator.OperatorShiftService;
import org.flexitech.projects.icpms.service.session.ParkingSessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/shifts")
@Slf4j
public class ShiftApiController {

	private final OperatorShiftService operatorShiftService;
	private final ParkingSessionService parkingSessionService;

	public ShiftApiController(OperatorShiftService operatorShiftService, ParkingSessionService parkingSessionService) {
		this.operatorShiftService = operatorShiftService;
		this.parkingSessionService = parkingSessionService;
	}

	@PostMapping("/start")
	public ResponseEntity<ApiResponse<OperatorShiftDTO>> startShift(@Valid @RequestBody StartShiftRequestDTO request,
			HttpServletRequest httpRequest) {

		Long operatorId = currentOperator().getOperator().getId();
		String gateIpAddress = httpRequest.getHeader(CommonConstants.GATE_IP_HEADER);

		if (!CommonValidators.validString(gateIpAddress)) {
			return ApiResponse.badRequest("Missing gate identifier.");
		}

		OperatorShiftDTO existing;
		try {
			existing = operatorShiftService.getActiveShiftByOperator(operatorId, gateIpAddress);
		} catch (IllegalStateException e) {
			log.warn("Shift start rejected for operator {}: {}", operatorId, e.getMessage());
			return ApiResponse.internalError(e.getMessage());
		}

		if (existing != null) {
			return ApiResponse.ok(existing, "Shift already active.");
		}

		OperatorShiftDTO toStart = new OperatorShiftDTO();
		toStart.setOperatorId(operatorId);
		toStart.setGateIpAddress(gateIpAddress);
		toStart.setOpeningCash(request.getOpeningCash());
		toStart.setRemark(request.getRemark());

		try {
			OperatorShiftDTO started = operatorShiftService.startShift(toStart);
			return ApiResponse.ok(started, "Shift started.");
		} catch (IllegalStateException e) {
			log.warn("Shift start failed for operator {}: {}", operatorId, e.getMessage());
			return ApiResponse.internalError(e.getMessage());
		}
	}
	
	@PostMapping("/end")
	public ResponseEntity<ApiResponse<OperatorShiftDTO>> endShift(@Valid @RequestBody EndShiftRequestDTO request,
			HttpServletRequest httpRequest) {
		Long operatorId = currentOperator().getOperator().getId();
		String gateIpAddress = httpRequest.getHeader(CommonConstants.GATE_IP_HEADER);
		if (!CommonValidators.validString(gateIpAddress)) {
			return ApiResponse.badRequest("Missing gate identifier.");
		}

		OperatorShiftDTO existing;
		try {
			existing = operatorShiftService.getActiveShiftByOperator(operatorId, gateIpAddress);
		} catch (IllegalStateException e) {
			log.warn("Shift end rejected for operator {}: {}", operatorId, e.getMessage());
			return ApiResponse.internalError(e.getMessage());
		}

		if (existing == null) {
			return ApiResponse.badRequest("No active shift found to end.");
		}

		existing.setGateIpAddress(gateIpAddress);
		existing.setClosingCash(request.getClosingCash());
		existing.setRemark(request.getRemark());

		try {
			OperatorShiftDTO ended = operatorShiftService.endShift(existing);
			return ApiResponse.ok(ended, "Shift ended.");
		} catch (IllegalStateException e) {
			log.warn("Shift end failed for operator {}: {}", operatorId, e.getMessage());
			return ApiResponse.internalError(e.getMessage());
		}
	}
	
	@GetMapping("/summary")
	public ResponseEntity<ApiResponse<OperatorShiftSummaryDTO>> getShiftSummary(HttpServletRequest httpRequest){
		OperatorShiftSummaryDTO data = new OperatorShiftSummaryDTO();
		try {
			Long operatorId = currentOperator().getOperator().getId();
			String gateIpAddress = httpRequest.getHeader(CommonConstants.GATE_IP_HEADER);
			if (!CommonValidators.validString(gateIpAddress)) {
				return ApiResponse.badRequest("Missing gate identifier.");
			}
			OperatorShiftDTO activeShift = operatorShiftService.getActiveShiftByOperator(operatorId, gateIpAddress);
			if(activeShift != null && CommonValidators.validLong(activeShift.getId())) {
				data = this.parkingSessionService.getShiftSummary(activeShift.getId());
			}else {
				return ApiResponse.unauthorized("There is no active shift!");
			}
		}catch (Exception e) {
			log.error("Error on getting shift summary:: {}", ExceptionUtils.getStackTrace(e));
			return ApiResponse.internalError(e.getMessage());
		}
		
		return ApiResponse.ok(data, "Get shift summary data success!");
	}
	
	private OperatorPrincipal currentOperator() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !(authentication.getPrincipal() instanceof OperatorPrincipal principal)) {
			throw new IllegalStateException("No authenticated operator in security context.");
		}
		return principal;
	}
}