package org.flexitech.projects.icpms.api.controllers;

import java.util.Optional;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.flexitech.projects.icpms.api.security.OperatorPrincipal;
import org.flexitech.projects.icpms.common.ApiErrorCode;
import org.flexitech.projects.icpms.common.CommonConstants;
import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.common.PlateNumberValidator;
import org.flexitech.projects.icpms.common.enums.ActiveStatus;
import org.flexitech.projects.icpms.dto.SearchResultDTO;
import org.flexitech.projects.icpms.dto.api.response.ApiResponse;
import org.flexitech.projects.icpms.dto.api.response.vehicle.VehicleDetailResponse;
import org.flexitech.projects.icpms.dto.gate.GateDTO;
import org.flexitech.projects.icpms.dto.member.MemberDTO;
import org.flexitech.projects.icpms.dto.vehicle.VehicleDTO;
import org.flexitech.projects.icpms.dto.vehicle.VehicleSearchDTO;
import org.flexitech.projects.icpms.service.audit_logs.VehicleAlertLogService;
import org.flexitech.projects.icpms.service.gate.GateService;
import org.flexitech.projects.icpms.service.member.MemberService;
import org.flexitech.projects.icpms.service.vehicle.VehicleService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/vehicle")
@RequiredArgsConstructor
@Slf4j
public class VehicleApiController {

	private final VehicleService vehicleService;

	private final MemberService memberService;
	
	private final VehicleAlertLogService vehicleAlertLogService;
	
	private final GateService gateService;

	@GetMapping("/get-by-number")
	public ResponseEntity<ApiResponse<VehicleDetailResponse>> getVehicleDetalByPlateNumber(@RequestParam String number, Authentication authentication, HttpServletRequest httpRequest) {
		try {
			
			OperatorPrincipal operator = currentOperator(authentication);
			if (operator == null) {
				return ApiResponse.error(HttpStatus.UNAUTHORIZED,
						"Operator context is required to record a parking session.");
			}
			
			String gateIpAddress = httpRequest.getHeader(CommonConstants.GATE_IP_HEADER);
			GateDTO gate = this.gateService.findByIpAddress(gateIpAddress);
			if (gate == null) {
				return ApiResponse.badRequest("Invalid gate.");
			}
			
			if (PlateNumberValidator.isUnknownOrInvalid(number)) {
				vehicleAlertLogService.logUnknownPlate(number, gate.getId(), operator.getOperator().getId());
				return ApiResponse.badRequest("Unknown or invalid plate number detected.", ApiErrorCode.UNKNOWN_PLATE);
			}

			Optional<VehicleDTO> vehicle = this.vehicleService.findByPlateNumber(number);

			if (vehicle.isEmpty()) {
				return ApiResponse.notFound("No vehicle found with this number.", ApiErrorCode.VEHICLE_NOT_FOUND);
			}

			VehicleDTO vehicleData = vehicle.get();
			MemberDTO member = null;
			if (CommonValidators.validLong(vehicleData.getMemberId())) {
				member = this.memberService.getMemberById(vehicleData.getMemberId());
			}

			boolean isBlacklist = this.vehicleService.isBlacklist(number);
			if (isBlacklist) {
				vehicleAlertLogService.logBlacklistDetected(number, vehicleData.getId(), gate.getId(), operator.getOperator().getId());
				return ApiResponse.badRequest("Blacklist vehicle detected!", ApiErrorCode.VEHICLE_BLACKLISTED);
			}

			VehicleDetailResponse response = new VehicleDetailResponse(vehicleData, member);
			response.setIsBlackList(isBlacklist);

			return ApiResponse.ok(response, "Get vehicle data success.");
		} catch (Exception e) {
			log.error("Error on get vehicle detail by number:: {}", ExceptionUtils.getStackTrace(e));
			return ApiResponse.internalError(e.getMessage());
		}
	}

	@GetMapping("/search")
	public ResponseEntity<ApiResponse<SearchResultDTO<VehicleDTO>>> searchVehicles(
			@ModelAttribute VehicleSearchDTO searchDTO,
			Pageable pageable,
			Authentication authentication,
			HttpServletRequest httpRequest) {
		try {
			OperatorPrincipal operator = currentOperator(authentication);
			if (operator == null) {
				return ApiResponse.error(HttpStatus.UNAUTHORIZED,
						"Operator context is required to search vehicles.");
			}

			if (CommonValidators.isValidObject(searchDTO.getFromSession()) && ActiveStatus.ACTIVE.getCode().equals(searchDTO.getFromSession())) {
				String gateIpAddress = httpRequest.getHeader(CommonConstants.GATE_IP_HEADER);
				GateDTO gate = this.gateService.findByIpAddress(gateIpAddress);
				if (gate == null) {
					return ApiResponse.badRequest("Invalid gate.");
				}
				if (!CommonValidators.validLong(gate.getParkingAreaId())) {
					return ApiResponse.badRequest("Gate is not assigned to a parking area.");
				}
				searchDTO.setParkingAreaId(gate.getParkingAreaId());
			}

			SearchResultDTO<VehicleDTO> result = this.vehicleService.searchVehicles(searchDTO, pageable);
			return ApiResponse.ok(result, "Search vehicles success.");
		} catch (Exception e) {
			log.error("Error on search vehicles:: {}", ExceptionUtils.getStackTrace(e));
			return ApiResponse.internalError(e.getMessage());
		}
	}

	private OperatorPrincipal currentOperator(Authentication authentication) {
		if (authentication != null && authentication.getPrincipal() instanceof OperatorPrincipal principal) {
			return principal;
		}
		return null;
	}
}