package org.flexitech.projects.icpms.service.gate;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.common.enums.ActiveStatus;
import org.flexitech.projects.icpms.dto.gate.GateDeviceDTO;
import org.flexitech.projects.icpms.persistence.entities.gate.Gate;
import org.flexitech.projects.icpms.persistence.entities.gate.GateDevice;
import org.flexitech.projects.icpms.persistence.entities.user.User;
import org.flexitech.projects.icpms.persistence.repositories.gate.GateDeviceRepository;
import org.flexitech.projects.icpms.persistence.repositories.gate.GateRepository;
import org.flexitech.projects.icpms.service.auth.AuthenticationService;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

@Service
public class GateDeviceServiceImpl implements GateDeviceService {

	private final GateDeviceRepository gateDeviceRepository;
	private final GateRepository gateRepository;
	private final AuthenticationService authenticationService;

	public GateDeviceServiceImpl(GateDeviceRepository gateDeviceRepository, GateRepository gateRepository,
			AuthenticationService authenticationService) {
		this.gateDeviceRepository = gateDeviceRepository;
		this.gateRepository = gateRepository;
		this.authenticationService = authenticationService;
	}

	@Override
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

		device.setDeviceType(dto.getDeviceType());
		device.setName(dto.getName());
		device.setDirection(dto.getDirection());
		device.setIpAddress(dto.getIpAddress());
		device.setPort(dto.getPort());
		device.setUsername(dto.getUsername());
		device.setModel(dto.getModel());
		device.setRemarks(dto.getRemarks());
		device.setStatus(CommonValidators.isValidObject(dto.getStatus()) ? dto.getStatus() : ActiveStatus.ACTIVE.getCode());

		// Only overwrite the password if a new one was actually entered, so
		// leaving the field blank on edit keeps the existing credential.
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
	public boolean deleteDevice(Long id) throws Exception {
		GateDevice device = this.gateDeviceRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Gate device doesn't exist!"));
		this.gateDeviceRepository.delete(device);
		return true;
	}
}