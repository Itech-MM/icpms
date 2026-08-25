package org.flexitech.projects.icpms.api.controllers;

import org.flexitech.projects.icpms.api.security.OperatorPrincipal;
import org.flexitech.projects.icpms.common.CommonConstants;
import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.dto.api.request.shift.StartShiftRequestDTO;
import org.flexitech.projects.icpms.dto.api.response.ApiResponse;
import org.flexitech.projects.icpms.dto.operator.OperatorShiftDTO;
import org.flexitech.projects.icpms.service.operator.OperatorShiftService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

	public ShiftApiController(OperatorShiftService operatorShiftService) {
		this.operatorShiftService = operatorShiftService;
	}

	@PostMapping("/start")
	public ApiResponse<OperatorShiftDTO> startShift(@Valid @RequestBody StartShiftRequestDTO request,
			HttpServletRequest httpRequest) {

		Long operatorId = currentOperator().getOperator().getId();
		String gateIpAddress = httpRequest.getHeader(CommonConstants.GATE_IP_HEADER);

		if (!CommonValidators.validString(gateIpAddress)) {
			return ApiResponse.error("Missing gate identifier.");
		}

		OperatorShiftDTO existing;
		try {
			existing = operatorShiftService.getActiveShiftByOperator(operatorId, gateIpAddress);
		} catch (IllegalStateException e) {
			log.warn("Shift start rejected for operator {}: {}", operatorId, e.getMessage());
			return ApiResponse.error(e.getMessage());
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
			return ApiResponse.error(e.getMessage());
		}
	}
	
	private OperatorPrincipal currentOperator() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !(authentication.getPrincipal() instanceof OperatorPrincipal principal)) {
			throw new IllegalStateException("No authenticated operator in security context.");
		}
		return principal;
	}
}