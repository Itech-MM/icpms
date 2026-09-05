package org.flexitech.projects.icpms.service.audit_logs;

import java.util.Date;
import java.util.stream.Collectors;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.flexitech.projects.icpms.common.CommonConstants;
import org.flexitech.projects.icpms.common.enums.ActiveStatus;
import org.flexitech.projects.icpms.common.enums.VehicleAlertType;
import org.flexitech.projects.icpms.common.utils.CommonUtils;
import org.flexitech.projects.icpms.dto.SearchResultDTO;
import org.flexitech.projects.icpms.dto.audit_logs.VehicleAlertLogDTO;
import org.flexitech.projects.icpms.dto.audit_logs.VehicleAlertLogSearchDTO;
import org.flexitech.projects.icpms.persistence.entities.audit_logs.VehicleAlertLog;
import org.flexitech.projects.icpms.persistence.repositories.audit_logs.VehicleAlertLogRepository;
import org.flexitech.projects.icpms.service.common.events.VehicleAlertCreatedEvent;
import org.flexitech.projects.icpms.service.specifications.audit_logs.VehicleAlertLogSpecification;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j

public class VehicleAlertLogServiceImpl implements VehicleAlertLogService {

	private final VehicleAlertLogRepository vehicleAlertLogRepository;

	private final ApplicationEventPublisher eventPublisher;

	@Override
	@Transactional
	public void logUnknownPlate(String plateNumber, Long gateId, Long operatorId) {
		save(VehicleAlertType.UNKNOWN_PLATE, plateNumber, null, null, null, gateId, operatorId,
				"Plate number not found in system: " + plateNumber);
	}

	@Override
	@Transactional
	public void logBlacklistDetected(String plateNumber, Long vehicleId, Long gateId, Long operatorId) {
		save(VehicleAlertType.BLACKLIST_DETECTED, plateNumber, vehicleId, null, null, gateId, operatorId,
				"Blacklisted vehicle detected: " + plateNumber);
	}

	@Override
	@Transactional
	public void logMemberExpired(String plateNumber, Long vehicleId, Long memberId, Long sessionId, Long gateId,
			Long operatorId) {
		save(VehicleAlertType.MEMBER_EXPIRED, plateNumber, vehicleId, memberId, sessionId, gateId, operatorId,
				"Member expired during active session for plate: " + plateNumber);
	}

	private void save(VehicleAlertType type, String plateNumber, Long vehicleId, Long memberId, Long sessionId,
			Long gateId, Long operatorId, String message) {
		try {
			VehicleAlertLog log = new VehicleAlertLog();
			log.setAlertType(type.getCode());
			log.setPlateNumber(plateNumber);
			log.setVehicleId(vehicleId);
			log.setMemberId(memberId);
			log.setSessionId(sessionId);
			log.setGateId(gateId);
			log.setOperatorId(operatorId);
			log.setMessage(message);
			log.setCreatedTime(new Date());
			log.setStatus(ActiveStatus.ACTIVE.getCode());
			VehicleAlertLog saved = vehicleAlertLogRepository.save(log);

			eventPublisher.publishEvent(new VehicleAlertCreatedEvent(saved));
		} catch (Exception e) {
			log.error("Failed to save vehicle alert log:: {}", ExceptionUtils.getStackTrace(e));
		}
	}

	@Override
	public SearchResultDTO<VehicleAlertLogDTO> searchVehicleAlertLogs(VehicleAlertLogSearchDTO searchDTO) {
		Specification<VehicleAlertLog> spec = VehicleAlertLogSpecification.withSearchCriteria(searchDTO);

		Pageable pageable = PageRequest.of(CommonUtils.getDefaultValue(searchDTO.getPageNo(), 0),
				CommonUtils.getDefaultValue(searchDTO.getLimit(), CommonConstants.ROW_PER_PAGE),
				Sort.by("createdTime").descending());

		Page<VehicleAlertLog> page = vehicleAlertLogRepository.findAll(spec, pageable);

		SearchResultDTO<VehicleAlertLogDTO> result = new SearchResultDTO<>();
		result.setPageNo(page.getNumber());
		result.setLimit(page.getSize());
		result.setTotalPage(page.getTotalPages());
		result.setTotalRecords((int) page.getTotalElements());
		result.setPageCount(page.getNumberOfElements());
		result.setHasNextPage(page.hasNext());
		result.setResults(page.getContent().stream().map(VehicleAlertLogDTO::new).collect(Collectors.toList()));
		return result;
	}

	@Override
	public void updateStatus(Integer status, Long id) {
		VehicleAlertLog log = vehicleAlertLogRepository.findById(id)
				.orElseThrow(()-> new RuntimeException("No log find."));
		log.setStatus(status);
		log.setUpdatedTime(new Date());
		this.vehicleAlertLogRepository.save(log);
	}
}