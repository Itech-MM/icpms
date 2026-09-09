package org.flexitech.projects.icpms.service.session;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.common.enums.ActiveStatus;
import org.flexitech.projects.icpms.common.enums.ParkingSessionStatus;
import org.flexitech.projects.icpms.common.enums.SlotStatus;
import org.flexitech.projects.icpms.common.utils.CommonUtils;
import org.flexitech.projects.icpms.dto.SearchResultDTO;
import org.flexitech.projects.icpms.dto.operator.OperatorShiftSummaryDTO;
import org.flexitech.projects.icpms.dto.session.ParkingSessionCloseDTO;
import org.flexitech.projects.icpms.dto.session.ParkingSessionCreateDTO;
import org.flexitech.projects.icpms.dto.session.ParkingSessionDTO;
import org.flexitech.projects.icpms.dto.session.ParkingSessionSearchDTO;
import org.flexitech.projects.icpms.dto.session.RecentSessionDTO;
import org.flexitech.projects.icpms.persistence.entities.gate.Gate;
import org.flexitech.projects.icpms.persistence.entities.member.Member;
import org.flexitech.projects.icpms.persistence.entities.operator.Operator;
import org.flexitech.projects.icpms.persistence.entities.operator.OperatorShift;
import org.flexitech.projects.icpms.persistence.entities.parking.ParkingArea;
import org.flexitech.projects.icpms.persistence.entities.session.ParkingSession;
import org.flexitech.projects.icpms.persistence.entities.slot.ParkingSlot;
import org.flexitech.projects.icpms.persistence.entities.tariff.Tariff;
import org.flexitech.projects.icpms.persistence.entities.vehicle.Vehicle;
import org.flexitech.projects.icpms.persistence.repositories.gate.GateRepository;
import org.flexitech.projects.icpms.persistence.repositories.operator.OperatorRepository;
import org.flexitech.projects.icpms.persistence.repositories.operator.OperatorShiftRepository;
import org.flexitech.projects.icpms.persistence.repositories.parking.ParkingAreaRepository;
import org.flexitech.projects.icpms.persistence.repositories.session.ParkingSessionRepository;
import org.flexitech.projects.icpms.persistence.repositories.slot.ParkingSlotRepository;
import org.flexitech.projects.icpms.persistence.repositories.tariff.TariffRepository;
import org.flexitech.projects.icpms.persistence.repositories.vehicle.VehicleRepository;
import org.flexitech.projects.icpms.service.specifications.session.ParkingSessionSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ParkingSessionServiceImpl implements ParkingSessionService {

	private final ParkingSessionRepository sessionRepository;
	private final VehicleRepository vehicleRepository;
	private final GateRepository gateRepository;
	private final ParkingSlotRepository slotRepository;
	private final OperatorRepository operatorRepository;
	private final OperatorShiftRepository operatorShiftRepository;
	private final ParkingAreaRepository parkingAreaRepository;
	private final TariffRepository tariffRepository;

	@Override
	public SearchResultDTO<ParkingSessionDTO> searchSessions(ParkingSessionSearchDTO searchDTO, Pageable pageable)
			throws Exception {
		Specification<ParkingSession> spec = ParkingSessionSpecification.withSearchCriteria(searchDTO);
		Page<ParkingSession> page = sessionRepository.findAll(spec, pageable);

		SearchResultDTO<ParkingSessionDTO> result = new SearchResultDTO<>();
		result.setPageNo(page.getNumber());
		result.setLimit(page.getSize());
		result.setTotalPage(page.getTotalPages());
		result.setTotalRecords((int) page.getTotalElements());
		result.setPageCount(page.getNumberOfElements());
		result.setHasNextPage(page.hasNext());
		result.setResults(page.getContent().stream().map(ParkingSessionDTO::new).collect(Collectors.toList()));
		return result;
	}

	@Override
	public ParkingSessionDTO getSessionById(Long id) throws Exception {
		ParkingSession session = this.sessionRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Parking session doesn't exist!"));
		return new ParkingSessionDTO(session);
	}

	@Override
	public long countActiveSessions() {
		return this.sessionRepository.countByStatus(ParkingSessionStatus.ACTIVE.getCode());
	}

	@Override
	public Optional<ParkingSessionDTO> findActiveByPlateNumber(String plateNumber) {
		return this.vehicleRepository.findByPlateNumberIgnoreCase(plateNumber).flatMap(
				vehicle -> this.sessionRepository.findFirstByVehicleIdAndStatusOrderByEntryTimeDesc(vehicle.getId(),
						ParkingSessionStatus.ACTIVE.getCode()))
				.map(ParkingSessionDTO::new);
	}

	@Override
	@Transactional
	public ParkingSessionDTO createEntry(ParkingSessionCreateDTO createDTO) throws Exception {
		Vehicle vehicle = this.vehicleRepository.findById(createDTO.getVehicleId())
				.orElseThrow(() -> new EntityNotFoundException("Vehicle doesn't exist!"));

		boolean alreadyActive = this.sessionRepository.findFirstByVehicleIdAndStatusOrderByEntryTimeDesc(
				createDTO.getVehicleId(), ParkingSessionStatus.ACTIVE.getCode()).isPresent();
		if (alreadyActive) {
			throw new IllegalStateException("This vehicle already has an active parking session.");
		}

		Gate entryGate = this.gateRepository.findById(createDTO.getEntryGateId())
				.orElseThrow(() -> new EntityNotFoundException("Entry gate doesn't exist!"));

		OperatorShift operatorShift = this.operatorShiftRepository.findById(createDTO.getEntryShiftId())
				.orElseThrow(() -> new EntityNotFoundException("Active shift doesn't exist!"));

		ParkingArea parkingArea = this.parkingAreaRepository.findById(createDTO.getParkingAreaId())
				.orElseThrow(() -> new EntityNotFoundException("Parking area doesn't exist!"));

		ParkingSlot slot = null;
		if (Boolean.TRUE.equals(parkingArea.getSlotTrackingEnabled())) {
			if (createDTO.getParkingSlotId() == null) {
				throw new IllegalArgumentException("This parking area requires a parking slot to be selected.");
			}
			slot = this.slotRepository.findById(createDTO.getParkingSlotId())
					.orElseThrow(() -> new EntityNotFoundException("Parking slot doesn't exist!"));

			if (slot.getStatus() != null && slot.getStatus() != SlotStatus.AVAILABLE.getCode()) {
				throw new IllegalStateException("Selected parking slot is not available.");
			}
		} else if (createDTO.getParkingSlotId() != null) {
			slot = this.slotRepository.findById(createDTO.getParkingSlotId())
					.orElseThrow(() -> new EntityNotFoundException("Parking slot doesn't exist!"));

			if (slot.getStatus() != null && slot.getStatus() != SlotStatus.AVAILABLE.getCode()) {
				throw new IllegalStateException("Selected parking slot is not available.");
			}
		}

		boolean isVipSlot;
		if (slot != null) {
			isVipSlot = Boolean.TRUE.equals(slot.getIsVip());
		} else {
			isVipSlot = vehicle.getMember() != null && Boolean.TRUE.equals(vehicle.getMember().getIsVip());
		}

		Integer available = parkingArea.getAvailableTotalSlot();
		if (available == null || available <= 0) {
			throw new IllegalStateException("No available slots in this parking area.");
		}
		parkingArea.setAvailableTotalSlot(available - 1);

		if (isVipSlot) {
			Integer availableVip = parkingArea.getAvailableTotalVipSlot();
			if (availableVip == null || availableVip <= 0) {
				throw new IllegalStateException("No available VIP slots in this parking area.");
			}
			parkingArea.setAvailableTotalVipSlot(availableVip - 1);
		}

		parkingArea.setUpdatedTime(new Date());
		this.parkingAreaRepository.save(parkingArea);

		ParkingSession session = new ParkingSession();
		session.setCreatedTime(new Date());
		session.setVehicle(vehicle);
		session.setEntryGate(entryGate);
		session.setEntryTime(new Date());
		session.setEntryPhotoUrl(createDTO.getEntryPhotoUrl());
		session.setStatus(ParkingSessionStatus.ACTIVE.getCode());
		session.setEntryShift(operatorShift);
		session.setParkingArea(parkingArea);

		if (createDTO.getOperatorId() != null) {
			Operator operator = this.operatorRepository.findById(createDTO.getOperatorId())
					.orElseThrow(() -> new EntityNotFoundException("Operator doesn't exist!"));
			session.setOperator(operator);
		}

		if (slot != null) {
			session.setParkingSlot(slot);
			slot.setStatus(SlotStatus.OCCUPIED.getCode());
			slot.setUpdatedTime(new Date());
			this.slotRepository.save(slot);
		}

		ParkingSession saved = this.sessionRepository.save(session);
		return new ParkingSessionDTO(saved);
	}

	@Override
	@Transactional
	public ParkingSessionDTO closeSession(ParkingSessionCloseDTO closeDTO) throws Exception {
		ParkingSession session = this.sessionRepository.findById(closeDTO.getSessionId())
				.orElseThrow(() -> new EntityNotFoundException("Parking session doesn't exist!"));

		if (!ParkingSessionStatus.ACTIVE.getCode().equals(session.getStatus())) {
			throw new IllegalStateException("This parking session has already been closed.");
		}

		Gate exitGate = this.gateRepository.findById(closeDTO.getExitGateId())
				.orElseThrow(() -> new EntityNotFoundException("Exit gate doesn't exist!"));

		if (closeDTO.getExitShiftId() != null) {
			OperatorShift exitShift = this.operatorShiftRepository.findById(closeDTO.getExitShiftId())
					.orElseThrow(() -> new EntityNotFoundException("Active shift doesn't exist!"));
			session.setExitShift(exitShift);
		}
		
		if(!CommonValidators.validLong(closeDTO.getTariffId())) {
			throw new IllegalArgumentException("Invalid tariff.");
		}
		
		Tariff tariff = tariffRepository.findById(closeDTO.getTariffId())
				.orElseThrow(()-> new IllegalArgumentException("Invalid tariff."));
		
		ParkingArea parkingArea = session.getParkingArea();
		
		if (!CommonValidators.isValidObject(parkingArea)) {
			throw new IllegalStateException("No parking area found.");
		}
		
		session.setExitGate(exitGate);
		session.setExitTime(new Date());
		session.setExitPhotoUrl(closeDTO.getExitPhotoUrl());
		session.setTotalAmount(closeDTO.getTotalAmount());
		session.setStatus(ParkingSessionStatus.COMPLETED.getCode());
		session.setMemberStatus(closeDTO.getIsMember() != null && closeDTO.getIsMember() ? ActiveStatus.ACTIVE.getCode() : ActiveStatus.INACTIVE.getCode());
		session.setFocStatus(closeDTO.getIsFoc() != null && closeDTO.getIsFoc() ? ActiveStatus.ACTIVE.getCode() : ActiveStatus.INACTIVE.getCode());
		session.setTariff(tariff);
		session.setDurationMinutes(CommonUtils.getDefaultValue(closeDTO.getDurationMinutes(), 0L));
		session.setRemark(closeDTO.getRemark());
		
		boolean isVip = false;
		
		if (session.getParkingSlot() != null) {
			ParkingSlot slot = session.getParkingSlot();
			slot.setStatus(SlotStatus.AVAILABLE.getCode());
			slot.setUpdatedTime(new Date());
			isVip = Boolean.TRUE.equals(slot.getIsVip());
			this.slotRepository.save(slot);
		}
		
		if (!isVip) {
			Member m = session.getVehicle().getMember();
			if (m != null) {
				isVip = Boolean.TRUE.equals(m.getIsVip());
			}
		}
		
		Integer available = parkingArea.getAvailableTotalSlot();
		Integer totalSlot = parkingArea.getTotalSlot() != null ? parkingArea.getTotalSlot() : 0;
		
		if (available == null) {
			available = totalSlot;
		}

		if (available > totalSlot) {
			log.warn("Available total slot greater than total slot!");
		}
		
		parkingArea.setAvailableTotalSlot(available + 1);

		if (isVip) {
			Integer availableVip = parkingArea.getAvailableTotalVipSlot();
			Integer totalVipSlot = parkingArea.getVipSlot() != null ? parkingArea.getVipSlot() : 0;

			if (availableVip == null) {
				availableVip = totalVipSlot;
			}

			if (availableVip > totalVipSlot) {
				log.warn("Available total vip slot greater than total vip slot!");
			}
			
			parkingArea.setAvailableTotalVipSlot(availableVip + 1);
		}

		parkingArea.setUpdatedTime(new Date());
		this.parkingAreaRepository.save(parkingArea);
		
		ParkingSession saved = this.sessionRepository.save(session);
		return new ParkingSessionDTO(saved);
	}

	@Override
	public long getElapsedMinutes(Long sessionId) throws Exception {
		ParkingSession session = this.sessionRepository.findById(sessionId)
				.orElseThrow(() -> new EntityNotFoundException("Parking session doesn't exist!"));
		long millis = new Date().getTime() - session.getEntryTime().getTime();
		return Math.max(0, millis / (60 * 1000));
	}

	@Override
	public OperatorShiftSummaryDTO getShiftSummary(Long shiftId) {
		OperatorShift shift = this.operatorShiftRepository.findById(shiftId)
				.orElseThrow(() -> new EntityNotFoundException("Shift doesn't exist!"));

		long incomplete = this.sessionRepository.countByEntryShiftIdAndStatus(shiftId,
				ParkingSessionStatus.ACTIVE.getCode());
		long completed = this.sessionRepository.countByExitShiftIdAndStatus(shiftId,
				ParkingSessionStatus.COMPLETED.getCode());
		BigDecimal totalAmount = this.sessionRepository.sumTotalAmountByShiftAndStatus(shiftId,
				ParkingSessionStatus.COMPLETED.getCode());

		OperatorShiftSummaryDTO summary = new OperatorShiftSummaryDTO();
		summary.setCode(shift.getCode());
		summary.setTotalTransactions((int) (incomplete + completed));
		summary.setTotalIncompleteTransactions((int) incomplete);
		summary.setTotalCompletedTransactions((int) completed);
		summary.setTotalAmount(totalAmount);
		summary.setTotalAmountDesc(CommonUtils.formatNumber(totalAmount));
		return summary;
	}

	@Override
	public SearchResultDTO<RecentSessionDTO> searchRecentVisitors(Long gateId, Pageable pageable) throws Exception {
		List<Integer> statuses = List.of(ParkingSessionStatus.ACTIVE.getCode(), ParkingSessionStatus.COMPLETED.getCode());
		Page<ParkingSession> page = sessionRepository.findRecentVisitorsByGate(statuses, gateId, pageable);

		SearchResultDTO<RecentSessionDTO> result = new SearchResultDTO<>();
		result.setPageNo(page.getNumber() + 1);
		result.setLimit(page.getSize());
		result.setTotalPage(page.getTotalPages());
		result.setTotalRecords((int) page.getTotalElements());
		result.setPageCount(page.getNumberOfElements());
		result.setHasNextPage(page.hasNext());
		result.setResults(page.getContent().stream().map(RecentSessionDTO::new).collect(Collectors.toList()));
		return result;
	}
}