package org.flexitech.projects.icpms.service.operator;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

import org.flexitech.projects.icpms.common.CommonConstants;
import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.common.enums.ParkingSessionStatus;
import org.flexitech.projects.icpms.common.enums.ShiftStatus;
import org.flexitech.projects.icpms.common.utils.QRCodeGenerator;
import org.flexitech.projects.icpms.dto.operator.OperatorShiftDTO;
import org.flexitech.projects.icpms.persistence.entities.gate.Gate;
import org.flexitech.projects.icpms.persistence.entities.operator.Operator;
import org.flexitech.projects.icpms.persistence.entities.operator.OperatorShift;
import org.flexitech.projects.icpms.persistence.repositories.gate.GateRepository;
import org.flexitech.projects.icpms.persistence.repositories.operator.OperatorRepository;
import org.flexitech.projects.icpms.persistence.repositories.operator.OperatorShiftRepository;
import org.flexitech.projects.icpms.persistence.repositories.session.ParkingSessionRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OperatorShiftServiceImpl implements OperatorShiftService {

	private final OperatorRepository operatorRepository;

	private final OperatorShiftRepository operatorShiftRepository;
	
	private final GateRepository gateRepository;
	
	private final ParkingSessionRepository parkingSessionRepository;
	
	private final QRCodeGenerator qrCodeGenerator;

	@Override
	public OperatorShiftDTO startShift(OperatorShiftDTO dto) {
		Operator operator = operatorRepository.findById(dto.getOperatorId())
				.orElseThrow(() -> new IllegalStateException("Operator not found: " + dto.getOperatorId()));

		if (!CommonValidators.validString(dto.getGateIpAddress())) {
			throw new IllegalStateException("Gate IP address is required to start a shift");
		}

		Gate gate = gateRepository.findByGateIpAddress(dto.getGateIpAddress())
				.orElseThrow(() -> new IllegalStateException("No gate registered for IP: " + dto.getGateIpAddress()));

		operatorShiftRepository
				.findFirstByOperatorIdAndGateIdAndShiftStatus(dto.getOperatorId(), gate.getId(), ShiftStatus.OPEN.getCode())
				.ifPresent(existing -> {
					throw new IllegalStateException(
							"Operator " + dto.getOperatorId() + " already has an active shift at gate " + gate.getId());
				});

		OperatorShift shift = new OperatorShift();
		shift.setOperator(operator);
		shift.setGate(gate);
		shift.setStartDateTime(new Date());
		shift.setShiftStatus(ShiftStatus.OPEN.getCode());
		shift.setOpeningCash(dto.getOpeningCash());
		shift.setRemark(dto.getRemark());
		
		String code = null;
		int attempts = 0;

		while (attempts < CommonConstants.MAX_RETRY) {
		    attempts++;
		    String candidate = qrCodeGenerator.generateRandomCouponCode();

		    if (operatorShiftRepository.findByCode(candidate).isEmpty()) {
		        code = candidate;
		        break;
		    }

		    log.warn("Attempt {} generated duplicate code, retrying...", attempts);
		}

		if (code == null) {
		    throw new IllegalStateException("Failed to generate a unique shift code after " + CommonConstants.MAX_RETRY + " attempts");
		}

		shift.setCode(code);

		OperatorShift saved = operatorShiftRepository.save(shift);

		log.info("Started shift {} for operator {}", saved.getId(), operator.getId());

		return new OperatorShiftDTO(saved);
	}

	@Override
	public OperatorShiftDTO endShift(OperatorShiftDTO dto) {
		OperatorShift shift = operatorShiftRepository.findById(dto.getId())
				.orElseThrow(() -> new IllegalStateException("Shift not found: " + dto.getId()));

		if (!ShiftStatus.OPEN.getCode().equals(shift.getShiftStatus())) {
			throw new IllegalStateException("Shift " + shift.getId() + " is not currently open");
		}

		if (!CommonValidators.validString(dto.getGateIpAddress())) {
			throw new IllegalStateException("Gate IP address is required to end a shift");
		}

		Gate gate = gateRepository.findByGateIpAddress(dto.getGateIpAddress())
				.orElseThrow(() -> new IllegalStateException("No gate registered for IP: " + dto.getGateIpAddress()));

		if (shift.getGate() != null && !shift.getGate().getId().equals(gate.getId())) {
			throw new IllegalStateException(
					"Shift " + shift.getId() + " was started at a different gate than " + dto.getGateIpAddress());
		}

		BigDecimal sessionRevenue = parkingSessionRepository.sumTotalAmountByShiftAndStatus(shift.getId(),
				ParkingSessionStatus.COMPLETED.getCode());
		BigDecimal openingCash = shift.getOpeningCash() != null ? shift.getOpeningCash() : BigDecimal.ZERO;
		BigDecimal expectedClosingCash = openingCash.add(sessionRevenue);

		shift.setEndDateTime(new Date());
		shift.setShiftStatus(ShiftStatus.CLOSED.getCode());
		shift.setClosingCash(expectedClosingCash);

		if (dto.getClosingCash() != null) {
			shift.setDiff(dto.getClosingCash().subtract(expectedClosingCash));
		} else {
			shift.setDiff(BigDecimal.ZERO);
		}

		if (dto.getRemark() != null) {
			shift.setRemark(dto.getRemark());
		}

		OperatorShift saved = operatorShiftRepository.save(shift);

		log.info("Ended shift {} for operator {} - expected: {}, counted: {}, diff: {}", saved.getId(),
				saved.getOperator() != null ? saved.getOperator().getId() : null, expectedClosingCash,
				dto.getClosingCash(), saved.getDiff());

		return new OperatorShiftDTO(saved);
	}

	@Override
	public OperatorShiftDTO getActiveShiftByOperator(Long operatorId, String gateIpAddress) {
		Gate gate = gateRepository.findByGateIpAddress(gateIpAddress)
				.orElseThrow(() -> new IllegalStateException("No gate registered for IP: " + gateIpAddress));

		Optional<OperatorShift> activeShift = operatorShiftRepository
				.findFirstByOperatorIdAndGateIdAndShiftStatus(operatorId, gate.getId(), ShiftStatus.OPEN.getCode());

		return activeShift.map(OperatorShiftDTO::new).orElse(null);
	}

}