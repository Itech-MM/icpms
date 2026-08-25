package org.flexitech.projects.icpms.api.controllers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.flexitech.projects.icpms.api.security.OperatorPrincipal;
import org.flexitech.projects.icpms.common.CommonConstants;
import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.dto.api.request.visitor.VisitorEntryRequestDTO;
import org.flexitech.projects.icpms.dto.api.request.visitor.VisitorExitRequestDTO;
import org.flexitech.projects.icpms.dto.api.request.visitor.VisitorWaiveRequestDTO;
import org.flexitech.projects.icpms.dto.api.response.ApiResponse;
import org.flexitech.projects.icpms.dto.api.response.visitor.VisitorExitPreviewResponseDTO;
import org.flexitech.projects.icpms.dto.api.response.visitor.VisitorExitResponseDTO;
import org.flexitech.projects.icpms.dto.api.response.visitor.VisitorLookupResponseDTO;
import org.flexitech.projects.icpms.dto.member.MemberDTO;
import org.flexitech.projects.icpms.dto.operator.OperatorShiftDTO;
import org.flexitech.projects.icpms.dto.payment.PaymentDTO;
import org.flexitech.projects.icpms.dto.session.ParkingSessionCloseDTO;
import org.flexitech.projects.icpms.dto.session.ParkingSessionCreateDTO;
import org.flexitech.projects.icpms.dto.session.ParkingSessionDTO;
import org.flexitech.projects.icpms.dto.tariff.TariffDTO;
import org.flexitech.projects.icpms.dto.vehicle.VehicleDTO;
import org.flexitech.projects.icpms.service.member.MemberService;
import org.flexitech.projects.icpms.service.operator.OperatorShiftService;
import org.flexitech.projects.icpms.service.payment.PaymentService;
import org.flexitech.projects.icpms.service.session.ParkingSessionService;
import org.flexitech.projects.icpms.service.tariff.TariffService;
import org.flexitech.projects.icpms.service.vehicle.VehicleService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/visitors")
@RequiredArgsConstructor
public class VisitorController {

	private final VehicleService vehicleService;
	private final MemberService memberService;
	private final ParkingSessionService parkingSessionService;
	private final TariffService tariffService;
	private final PaymentService paymentService;
	private final OperatorShiftService operatorShiftService;

	@GetMapping("/lookup")
	public ApiResponse<VisitorLookupResponseDTO> lookup(@RequestParam String plateNumber) throws Exception {
		VisitorLookupResponseDTO result = new VisitorLookupResponseDTO();

		Optional<ParkingSessionDTO> activeSession = parkingSessionService.findActiveByPlateNumber(plateNumber);
		result.setHasActiveSession(activeSession.isPresent());
		result.setActiveSession(activeSession.orElse(null));

		Optional<VehicleDTO> vehicle = vehicleService.findByPlateNumber(plateNumber);
		if (vehicle.isPresent() && CommonValidators.validLong(vehicle.get().getMemberId())) {
			MemberDTO member = memberService.getMemberById(vehicle.get().getMemberId());
			result.setMember(true);
			result.setMemberName(member.getName());
			result.setVip(Boolean.TRUE.equals(member.getIsVip()));
		}

		return ApiResponse.ok(result);
	}

	@PostMapping("/entry")
	public ApiResponse<ParkingSessionDTO> saveEntry(@Valid @RequestBody VisitorEntryRequestDTO request,
			Authentication authentication, HttpServletRequest httpRequest) throws Exception {

		VehicleDTO vehicle = vehicleService.findOrCreateByPlateNumber(request.getPlateNumber(),
				request.getVehicleType());

		Long tariffId = request.getTariffId();
		if (!CommonValidators.validLong(tariffId)) {
			List<TariffDTO> activeTariffs = tariffService.findAllActiveTariffs();
			if (activeTariffs.isEmpty()) {
				throw new EntityNotFoundException("No active tariff is configured - please set one up first.");
			}
			tariffId = activeTariffs.get(0).getId();
		}

		OperatorPrincipal operator = currentOperator(authentication);
		OperatorShiftDTO activeShift = resolveActiveShift(operator, httpRequest);

		ParkingSessionCreateDTO createDTO = new ParkingSessionCreateDTO();
		createDTO.setVehicleId(vehicle.getId());
		createDTO.setEntryGateId(activeShift.getGateId());
		
		createDTO.setTariffId(tariffId);
		createDTO.setOperatorId(operator.getOperator().getId());
		createDTO.setEntryShiftId(activeShift.getId());

		ParkingSessionDTO session = parkingSessionService.createEntry(createDTO);

		return ApiResponse.ok(session, "Entry saved.");
	}

	@GetMapping("/exit-preview")
	public ApiResponse<VisitorExitPreviewResponseDTO> exitPreview(@RequestParam String plateNumber) throws Exception {
		ParkingSessionDTO session = parkingSessionService.findActiveByPlateNumber(plateNumber)
				.orElseThrow(() -> new EntityNotFoundException("No active parking session found for this plate."));

		long durationMinutes = parkingSessionService.getElapsedMinutes(session.getId());
		BigDecimal amountDue = tariffService.calculateFee(session.getTariffId(), durationMinutes);

		VisitorExitPreviewResponseDTO preview = new VisitorExitPreviewResponseDTO(session.getId(),
				session.getPlateNumber(), session.getEntryTime(), durationMinutes, session.getTariffName(), amountDue);

		return ApiResponse.ok(preview);
	}

	@PostMapping("/exit")
	public ApiResponse<VisitorExitResponseDTO> exit(@Valid @RequestBody VisitorExitRequestDTO request,
	        Authentication authentication, HttpServletRequest httpRequest) throws Exception {
	    ParkingSessionDTO activeSession = parkingSessionService.findActiveByPlateNumber(request.getPlateNumber())
	            .orElseThrow(() -> new EntityNotFoundException("No active parking session found for this plate."));

	    long durationMinutes = parkingSessionService.getElapsedMinutes(activeSession.getId());
	    BigDecimal amountDue = tariffService.calculateFee(activeSession.getTariffId(), durationMinutes);

	    OperatorPrincipal operator = currentOperator(authentication);
	    OperatorShiftDTO activeShift = resolveActiveShift(operator, httpRequest);

	    ParkingSessionCloseDTO closeDTO = new ParkingSessionCloseDTO();
	    closeDTO.setSessionId(activeSession.getId());
	    closeDTO.setExitGateId(activeShift.getGateId());
	    closeDTO.setTotalAmount(amountDue);
	    closeDTO.setExitShiftId(activeShift.getId());

	    ParkingSessionDTO closedSession = parkingSessionService.closeSession(closeDTO);
	    PaymentDTO payment = paymentService.recordPayment(activeSession.getId(), amountDue, request.getPaymentMethod(), request.getReferenceNo());

	    return ApiResponse.ok(new VisitorExitResponseDTO(closedSession, payment), "Exit completed.");
	}

	@PostMapping("/exit/waive")
	public ApiResponse<VisitorExitResponseDTO> waive(@Valid @RequestBody VisitorWaiveRequestDTO request,
			Authentication authentication, HttpServletRequest httpRequest) throws Exception {
		ParkingSessionDTO activeSession = parkingSessionService.findActiveByPlateNumber(request.getPlateNumber())
				.orElseThrow(() -> new EntityNotFoundException("No active parking session found for this plate."));
	    OperatorPrincipal operator = currentOperator(authentication);
	    OperatorShiftDTO activeShift = resolveActiveShift(operator, httpRequest);

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
	}

	private OperatorPrincipal currentOperator(Authentication authentication) {
		if (authentication != null && authentication.getPrincipal() instanceof OperatorPrincipal principal) {
			return principal;
		}
		return null;
	}

	private OperatorShiftDTO resolveActiveShift(OperatorPrincipal operator, HttpServletRequest httpRequest)
			throws Exception {
		if (operator == null) {
			throw new IllegalStateException("Operator context is required to record a parking session.");
		}
		String gateIpAddress = httpRequest.getHeader(CommonConstants.GATE_IP_HEADER);
		OperatorShiftDTO activeShift = operatorShiftService.getActiveShiftByOperator(operator.getOperator().getId(),
				gateIpAddress);
		if (activeShift == null) {
			throw new IllegalStateException("No active shift found - please open a shift first.");
		}
		return activeShift;
	}
}