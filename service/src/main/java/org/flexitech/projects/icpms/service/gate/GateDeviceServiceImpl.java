package org.flexitech.projects.icpms.service.gate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.common.enums.ActiveStatus;
import org.flexitech.projects.icpms.common.enums.DeviceConnectionType;
import org.flexitech.projects.icpms.dto.api.request.gate_device.GateDeviceBatchStatusRequest;
import org.flexitech.projects.icpms.dto.api.request.gate_device.GateDeviceStatusUpdateRequest;
import org.flexitech.projects.icpms.dto.api.response.gate_device.GateDeviceBatchStatusResponse;
import org.flexitech.projects.icpms.dto.gate.GateDeviceDTO;
import org.flexitech.projects.icpms.dto.gate.GateDeviceDiagnosisDTO;
import org.flexitech.projects.icpms.persistence.entities.gate.Gate;
import org.flexitech.projects.icpms.persistence.entities.gate.GateDevice;
import org.flexitech.projects.icpms.persistence.entities.user.User;
import org.flexitech.projects.icpms.persistence.repositories.gate.GateDeviceRepository;
import org.flexitech.projects.icpms.persistence.repositories.gate.GateRepository;
import org.flexitech.projects.icpms.service.auth.AuthenticationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional(readOnly = true)
public class GateDeviceServiceImpl implements GateDeviceService {

	private final GateDeviceRepository gateDeviceRepository;
	private final GateRepository gateRepository;
	private final AuthenticationService authenticationService;

	private final GateDeviceDiagnosisService deviceDiagnosisService;
	
	public GateDeviceServiceImpl(GateDeviceRepository gateDeviceRepository, GateRepository gateRepository,
			AuthenticationService authenticationService, GateDeviceDiagnosisService deviceDiagnosisService) {
		this.gateDeviceRepository = gateDeviceRepository;
		this.gateRepository = gateRepository;
		this.authenticationService = authenticationService;
		this.deviceDiagnosisService = deviceDiagnosisService;
	}

	@Override
	@Transactional
	public GateDeviceDTO manageDevice(GateDeviceDTO dto) throws Exception {
		GateDevice device;
		User user = this.authenticationService.getLoggedInUser();
		String existingPassword = null;

		if (CommonValidators.validLong(dto.getId())) {
			device = this.gateDeviceRepository.findById(dto.getId())
					.orElseThrow(() -> new EntityNotFoundException("Gate device doesn't exist!"));
			existingPassword = device.getPassword();
			device.setUpdatedTime(new Date());
			device.setUpdatedBy(user);
		} else {
			device = new GateDevice();
			device.setCreatedTime(new Date());
			device.setCreatedBy(user);
			Gate gate = this.gateRepository.findById(dto.getGateId())
					.orElseThrow(() -> new EntityNotFoundException("Gate doesn't exist!"));
			device.setGate(gate);
		}

		if (dto.getConnectionType() != null && dto.getConnectionType().equals(DeviceConnectionType.LAN.getCode())) {
			if (!CommonValidators.validString(dto.getIpAddress())) {
				throw new IllegalArgumentException("IP address is required for LAN devices!");
			}
		} else if (dto.getConnectionType() != null && dto.getConnectionType().equals(DeviceConnectionType.SERIAL.getCode())) {
			if (!CommonValidators.validString(dto.getComPort())) {
				throw new IllegalArgumentException("COM port is required for serial devices!");
			}
		}

		device.setDeviceType(dto.getDeviceType());
		device.setConnectionType(dto.getConnectionType());
		device.setName(dto.getName());
		device.setDirection(dto.getDirection());
		device.setIpAddress(dto.getIpAddress());
		device.setPort(dto.getPort());
		device.setComPort(dto.getComPort());
		device.setBaudRate(dto.getBaudRate());
		device.setUsername(dto.getUsername());
		device.setModel(dto.getModel());
		device.setFirmwareVersion(dto.getFirmwareVersion());
		device.setSerialNumber(dto.getSerialNumber());
		device.setRemarks(dto.getRemarks());
		device.setStatus(CommonValidators.isValidObject(dto.getStatus()) ? dto.getStatus() : ActiveStatus.ACTIVE.getCode());

		device.setAccessUrl(dto.getAccessUrl());

		if (CommonValidators.validString(dto.getPassword())) {
			device.setPassword(dto.getPassword());
		} else {
			device.setPassword(existingPassword);
		}

		GateDevice saved = this.gateDeviceRepository.save(device);
		return new GateDeviceDTO(saved);
	}

	@Override
	public GateDeviceDTO getDeviceById(Long id) throws Exception {
		GateDevice device = this.gateDeviceRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Gate device doesn't exist!"));
		return new GateDeviceDTO(device);
	}

	@Override
	public List<GateDeviceDTO> getDevicesByGate(Long gateId) {
		return this.gateDeviceRepository.findByGateIdOrderByIdAsc(gateId)
				.stream().map(GateDeviceDTO::new).collect(Collectors.toList());
	}

	@Override
	@Transactional
	public boolean deleteDevice(Long id) throws Exception {
		GateDevice device = this.gateDeviceRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Gate device doesn't exist!"));
		this.gateDeviceRepository.delete(device);
		return true;
	}

	@Override
	@Transactional
	public void updateGateDeviceStatus(GateDeviceDTO dto) throws Exception {
		if(!CommonValidators.validLong(dto.getId())) {
			throw new IllegalArgumentException("Invalid request.");
		}
		
		GateDevice device = this.gateDeviceRepository.findById(dto.getId())
				.orElseThrow(()-> new RuntimeException("Invalid device."));
		
		device.setLastHealthStatus(dto.getLastHealthStatus());
		device.setLastCheckedAt(LocalDateTime.now());
		device.setLastLatencyMs(dto.getLastLatencyMs());
		device.setLastStatusNote(dto.getLastStatusNote());
		
		GateDevice updated =  this.gateDeviceRepository.save(device);
		
		GateDeviceDiagnosisDTO dia = new GateDeviceDiagnosisDTO();
		dia.setGateDeviceId(updated.getId());
		dia.setHealthStatus(dto.getLastHealthStatus());
		dia.setLatencyMs(dto.getLastLatencyMs());
		dia.setCheckedAt(LocalDateTime.now());
		dia.setStatusNote(dto.getLastStatusNote());
		
		dia.setDeviceResponse("");
		
		this.deviceDiagnosisService.logDiagnosis(dia);
	}
	
	@Override
	@Transactional
	public GateDeviceBatchStatusResponse updateGateDeviceStatusBatch(
	        GateDeviceBatchStatusRequest request) {

	    List<GateDeviceStatusUpdateRequest> items = request.getDevices();

	    List<Long> ids = items.stream()
	            .map(GateDeviceStatusUpdateRequest::getDeviceId)
	            .filter(Objects::nonNull)
	            .toList();

	    List<GateDevice> devices = gateDeviceRepository.findAllById(ids);
	    Map<Long, GateDevice> deviceMap = devices.stream()
	            .collect(Collectors.toMap(GateDevice::getId, d -> d));

	    LocalDateTime now = LocalDateTime.now();
	    List<GateDeviceDiagnosisDTO> diagnoses = new ArrayList<>();
	    List<GateDeviceBatchStatusResponse.FailedItem> failed = new ArrayList<>();
	    List<GateDevice> toSave = new ArrayList<>();

	    for (GateDeviceStatusUpdateRequest item : items) {
	        GateDevice device = deviceMap.get(item.getDeviceId());
	        if (device == null) {
	            failed.add(new GateDeviceBatchStatusResponse.FailedItem(
	                    item.getDeviceId(), "Device not found"));
	            continue;
	        }

	        device.setLastHealthStatus(item.getHealthStatus());
	        device.setLastCheckedAt(now);
	        device.setLastLatencyMs(item.getLatencyMs());
	        device.setLastStatusNote(item.getStatusNote());
	        toSave.add(device);

	        GateDeviceDiagnosisDTO dia = new GateDeviceDiagnosisDTO();
	        dia.setGateDeviceId(device.getId());
	        dia.setHealthStatus(item.getHealthStatus());
	        dia.setLatencyMs(item.getLatencyMs());
	        dia.setCheckedAt(now);
	        dia.setStatusNote(item.getStatusNote());
	        dia.setDeviceResponse("");
	        diagnoses.add(dia);
	    }

	    if (!toSave.isEmpty()) {
	        gateDeviceRepository.saveAll(toSave);
	    }
	    if (!diagnoses.isEmpty()) {
	        diagnoses.forEach(deviceDiagnosisService::logDiagnosis);
	    }

	    return new GateDeviceBatchStatusResponse(
	            items.size(), toSave.size(), failed);
	}
	
}