package org.flexitech.projects.icpms.api.controllers;

import java.math.BigDecimal;
import java.util.Optional;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.flexitech.projects.icpms.api.security.OperatorPrincipal;
import org.flexitech.projects.icpms.common.ApiErrorCode;
import org.flexitech.projects.icpms.common.CommonConstants;
import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.common.PlateNumberValidator;
import org.flexitech.projects.icpms.common.utils.CommonUtils;
import org.flexitech.projects.icpms.dto.SearchResultDTO;
import org.flexitech.projects.icpms.dto.api.request.visitor.VisitorEntryRequestDTO;
import org.flexitech.projects.icpms.dto.api.request.visitor.VisitorExitRequestDTO;
import org.flexitech.projects.icpms.dto.api.request.visitor.VisitorWaiveRequestDTO;
import org.flexitech.projects.icpms.dto.api.response.ApiResponse;
import org.flexitech.projects.icpms.dto.api.response.visitor.VisitorExitPreviewResponseDTO;
import org.flexitech.projects.icpms.dto.api.response.visitor.VisitorExitResponseDTO;
import org.flexitech.projects.icpms.dto.gate.GateDTO;
import org.flexitech.projects.icpms.dto.member.MemberDTO;
import org.flexitech.projects.icpms.dto.operator.OperatorShiftDTO;
import org.flexitech.projects.icpms.dto.parking.ParkingAreaDTO;
import org.flexitech.projects.icpms.dto.payment.PaymentDTO;
import org.flexitech.projects.icpms.dto.session.ParkingSessionCloseDTO;
import org.flexitech.projects.icpms.dto.session.ParkingSessionCreateDTO;
import org.flexitech.projects.icpms.dto.session.ParkingSessionDTO;
import org.flexitech.projects.icpms.dto.session.RecentSessionDTO;
import org.flexitech.projects.icpms.dto.vehicle.VehicleDTO;
import org.flexitech.projects.icpms.service.audit_logs.VehicleAlertLogService;
import org.flexitech.projects.icpms.service.gate.GateService;
import org.flexitech.projects.icpms.service.member.MemberService;
import org.flexitech.projects.icpms.service.operator.OperatorShiftService;
import org.flexitech.projects.icpms.service.parking.ParkingAreaService;
import org.flexitech.projects.icpms.service.payment.PaymentService;
import org.flexitech.projects.icpms.service.session.ParkingSessionService;
import org.flexitech.projects.icpms.service.tariff.TariffService;
import org.flexitech.projects.icpms.service.vehicle.VehicleService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/visitors")
@RequiredArgsConstructor
@Slf4j
public class VisitorApiController {

	private final VehicleService vehicleService;
	private final MemberService memberService;
	private final ParkingSessionService parkingSessionService;
	private final TariffService tariffService;
	private final PaymentService paymentService;
	private final OperatorShiftService operatorShiftService;

	private final ParkingAreaService parkingAreaService;

	private final GateService gateService;

	private final VehicleAlertLogService vehicleAlertLogService;

	@PostMapping("/entry")
	public ResponseEntity<ApiResponse<ParkingSessionDTO>> saveEntry(@Valid @RequestBody VisitorEntryRequestDTO request,
			Authentication authentication, HttpServletRequest httpRequest) {
		try {
			OperatorPrincipal operator = currentOperator(authentication);
			if (operator == null) {
				return ApiResponse.error(HttpStatus.UNAUTHORIZED,
						"Operator context is required to record a parking session.");
			}

			String gateIpAddress = httpRequest.getHeader(CommonConstants.GATE_IP_HEADER);
			GateDTO gate = this.gateService.findByIpAddress(gateIpAddress);

			if (gate == null) {
				return ApiResponse.badRequest("No active shift found - please open a shift first.");
			}

			if (vehicleService.isBlacklist(request.getPlateNumber())) {
				vehicleAlertLogService.logBlacklistDetected(request.getPlateNumber(), null, gate.getId(),
						operator.getOperator().getId());
				return ApiResponse.error(HttpStatus.FORBIDDEN, "This vehicle is blacklisted. Entry denied.");
			}

			VehicleDTO vehicle = vehicleService.findOrCreateByPlateNumber(request.getPlateNumber(),
					request.getVehicleType());

			OperatorShiftDTO activeShift = resolveActiveShift(operator, httpRequest);
			if (activeShift == null) {
				return ApiResponse.badRequest("No active shift found - please open a shift first.");
			}

			ParkingAreaDTO parkingArea = parkingAreaService.getByGateId(gate.getId());

			if (parkingArea == null) {
				return ApiResponse.error(HttpStatus.NO_CONTENT, "No active parking area.");
			}

			ParkingSessionCreateDTO createDTO = new ParkingSessionCreateDTO();
			createDTO.setVehicleId(vehicle.getId());
			createDTO.setEntryGateId(activeShift.getGateId());
			createDTO.setOperatorId(operator.getOperator().getId());
			createDTO.setEntryShiftId(activeShift.getId());
			createDTO.setParkingSlotId(request.getParkingSlotId());
			createDTO.setParkingAreaId(parkingArea.getId());

			ParkingSessionDTO session = parkingSessionService.createEntry(createDTO);

			return ApiResponse.ok(session, "Entry saved.");
		} catch (Exception e) {
			log.error("Error on visitor entry:: {}", ExceptionUtils.getStackTrace(e));
			return ApiResponse.internalError(e.getMessage());
		}
	}

	@GetMapping("/exit-preview")
	public ResponseEntity<ApiResponse<VisitorExitPreviewResponseDTO>> exitPreview(@RequestParam String plateNumber,
			HttpServletRequest httpRequest, Authentication authentication) {
		try {
			OperatorPrincipal operator = currentOperator(authentication);
			if (operator == null) {
				return ApiResponse.error(HttpStatus.UNAUTHORIZED,
						"Operator context is required to record a parking session.");
			}

			OperatorShiftDTO activeShift = resolveActiveShift(operator, httpRequest);
			if (activeShift == null) {
				return ApiResponse.badRequest("No active shift found - please open a shift first.");
			}

			Optional<ParkingSessionDTO> activeSession = parkingSessionService.findActiveByPlateNumber(plateNumber);
			if (activeSession.isEmpty()) {
				return ApiResponse.notFound("No active parking session found for this plate.");
			}

			String gateIpAddress = httpRequest.getHeader(CommonConstants.GATE_IP_HEADER);
			GateDTO gate = this.gateService.findByIpAddress(gateIpAddress);
			if (gate == null) {
				return ApiResponse.badRequest("Invalid gate.");
			}

			if (PlateNumberValidator.isUnknownOrInvalid(plateNumber)) {
				vehicleAlertLogService.logUnknownPlate(plateNumber, gate.getId(), operator.getOperator().getId());
				return ApiResponse.badRequest("Unknown or invalid plate number detected.", ApiErrorCode.UNKNOWN_PLATE);
			}

			ParkingSessionDTO session = activeSession.get();

			long durationMinutes = parkingSessionService.getElapsedMinutes(session.getId());
			BigDecimal amountDue = tariffService.calculateFee(session.getParkingAreaId(), durationMinutes);

			VisitorExitPreviewResponseDTO preview = new VisitorExitPreviewResponseDTO(session.getId(),
					session.getPlateNumber(), session.getEntryTime(), durationMinutes, session.getTariffName(),
					amountDue, CommonUtils.formatNumber(amountDue));

			return ApiResponse.ok(preview);
		} catch (Exception e) {
			log.error("Error on visitor exit preview:: {}", ExceptionUtils.getStackTrace(e));
			return ApiResponse.internalError(e.getMessage());
		}
	}

	@PostMapping("/exit")
	public ResponseEntity<ApiResponse<VisitorExitResponseDTO>> exit(@Valid @RequestBody VisitorExitRequestDTO request,
			Authentication authentication, HttpServletRequest httpRequest) {
		try {
			Optional<ParkingSessionDTO> activeSessionOpt = parkingSessionService
					.findActiveByPlateNumber(request.getPlateNumber());
			if (activeSessionOpt.isEmpty()) {
				return ApiResponse.notFound("No active parking session found for this plate.");
			}
			ParkingSessionDTO activeSession = activeSessionOpt.get();

			OperatorPrincipal operator = currentOperator(authentication);
			if (operator == null) {
				return ApiResponse.error(HttpStatus.UNAUTHORIZED,
						"Operator context is required to record a parking session.");
			}

			OperatorShiftDTO activeShift = resolveActiveShift(operator, httpRequest);
			if (activeShift == null) {
				return ApiResponse.badRequest("No active shift found - please open a shift first.");
			}

			if (request.getIsMember()) {
				Optional<VehicleDTO> vehicle = vehicleService.findByPlateNumber(request.getPlateNumber());
				if (vehicle.isPresent() && CommonValidators.validLong(vehicle.get().getMemberId())) {
					MemberDTO member = memberService.getMemberById(vehicle.get().getMemberId());
					if (member.getIsExpired()) {
						vehicleAlertLogService.logMemberExpired(request.getPlateNumber(), vehicle.get().getId(),
								member.getId(), activeSession.getId(), activeShift.getGateId(),
								operator.getOperator().getId());
					}
				}
			}

			ParkingAreaDTO parkingArea = parkingAreaService.getParkingAreaById(activeSession.getParkingAreaId());

			if (parkingArea == null) {
				return ApiResponse.badRequest("Invalid parking area.");
			}

			Long tariffId = parkingArea.getTariffId();

			if (!CommonValidators.validLong(tariffId)) {
				return ApiResponse.badRequest("Parking area has no active tariff assign.");
			}

			long durationMinutes = parkingSessionService.getElapsedMinutes(activeSession.getId());
			BigDecimal amountDue = !request.getIsFoc() && !request.getIsMember()
					? tariffService.calculateFee(tariffId, durationMinutes)
					: BigDecimal.ZERO;

			ParkingSessionCloseDTO closeDTO = new ParkingSessionCloseDTO();
			closeDTO.setSessionId(activeSession.getId());
			closeDTO.setExitGateId(activeShift.getGateId());
			closeDTO.setTotalAmount(amountDue);
			closeDTO.setExitShiftId(activeShift.getId());
			closeDTO.setIsMember(request.getIsMember());
			closeDTO.setIsFoc(request.getIsFoc());
			closeDTO.setTariffId(tariffId);
			closeDTO.setDurationMinutes(durationMinutes);
			closeDTO.setRemark(request.getRemark());

			ParkingSessionDTO closedSession = parkingSessionService.closeSession(closeDTO);
			PaymentDTO payment = paymentService.recordPayment(activeSession.getId(), amountDue,
					request.getPaymentMethod(), request.getReferenceNo());

			return ApiResponse.ok(new VisitorExitResponseDTO(closedSession, payment), "Exit completed.");
		} catch (Exception e) {
			log.error("Error on visitor exit:: {}", ExceptionUtils.getStackTrace(e));
			return ApiResponse.internalError(e.getMessage());
		}
	}

	@PostMapping("/exit/waive")
	public ResponseEntity<ApiResponse<VisitorExitResponseDTO>> waive(@Valid @RequestBody VisitorWaiveRequestDTO request,
			Authentication authentication, HttpServletRequest httpRequest) {
		try {
			Optional<ParkingSessionDTO> activeSessionOpt = parkingSessionService
					.findActiveByPlateNumber(request.getPlateNumber());
			if (activeSessionOpt.isEmpty()) {
				return ApiResponse.notFound("No active parking session found for this plate.");
			}
			ParkingSessionDTO activeSession = activeSessionOpt.get();

			OperatorPrincipal operator = currentOperator(authentication);
			if (operator == null) {
				return ApiResponse.error(HttpStatus.UNAUTHORIZED,
						"Operator context is required to record a parking session.");
			}

			OperatorShiftDTO activeShift = resolveActiveShift(operator, httpRequest);
			if (activeShift == null) {
				return ApiResponse.badRequest("No active shift found - please open a shift first.");
			}

			ParkingSessionCloseDTO closeDTO = new ParkingSessionCloseDTO();
			closeDTO.setSessionId(activeSession.getId());
			closeDTO.setExitGateId(activeShift.getGateId());
			closeDTO.setTotalAmount(BigDecimal.ZERO);
			closeDTO.setExitShiftId(activeShift.getId());

			ParkingSessionDTO closedSession = parkingSessionService.closeSession(closeDTO);

			String supervisorName = authentication.getPrincipal() instanceof OperatorPrincipal principal
					? principal.getOperator().getName()
					: "supervisor";
			String reference = "WAIVED by " + supervisorName
					+ (CommonValidators.validString(request.getReason()) ? " - " + request.getReason() : "");

			PaymentDTO payment = paymentService.recordPayment(activeSession.getId(), BigDecimal.ZERO, null, reference);

			return ApiResponse.ok(new VisitorExitResponseDTO(closedSession, payment), "Fee waived, exit completed.");
		} catch (Exception e) {
			log.error("Error on visitor waive:: {}", ExceptionUtils.getStackTrace(e));
			return ApiResponse.internalError(e.getMessage());
		}
	}

	@GetMapping("/recent")
	public ResponseEntity<ApiResponse<SearchResultDTO<RecentSessionDTO>>> recentVisitors(@RequestParam Integer page,
			HttpServletRequest httpRequest) {
		try {
			String gateIpAddress = httpRequest.getHeader(CommonConstants.GATE_IP_HEADER);
			GateDTO gate = this.gateService.findByIpAddress(gateIpAddress);

			if (gate == null) {
				return ApiResponse.badRequest("Invalid gate.");
			}

			Pageable pageable = PageRequest.of(page - 1, CommonConstants.ROW_PER_PAGE);

			SearchResultDTO<RecentSessionDTO> result = parkingSessionService.searchRecentVisitors(gate.getId(),
					pageable);

			return ApiResponse.ok(result, "Recent visitors retrieved.");
		} catch (Exception e) {
			log.error("Error on recent visitors:: {}", ExceptionUtils.getStackTrace(e));
			return ApiResponse.internalError(e.getMessage());
		}
	}

	private OperatorPrincipal currentOperator(Authentication authentication) {
		if (authentication != null && authentication.getPrincipal() instanceof OperatorPrincipal principal) {
			return principal;
		}
		return null;
	}

	private OperatorShiftDTO resolveActiveShift(OperatorPrincipal operator, HttpServletRequest httpRequest)
			throws Exception {
		String gateIpAddress = httpRequest.getHeader(CommonConstants.GATE_IP_HEADER);
		return operatorShiftService.getActiveShiftByOperator(operator.getOperator().getId(), gateIpAddress);
	}
}