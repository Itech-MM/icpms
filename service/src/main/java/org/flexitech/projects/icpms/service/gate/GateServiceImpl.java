package org.flexitech.projects.icpms.service.gate;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.common.enums.ActiveStatus;
import org.flexitech.projects.icpms.dto.SearchResultDTO;
import org.flexitech.projects.icpms.dto.gate.GateDTO;
import org.flexitech.projects.icpms.dto.gate.GateSearchDTO;
import org.flexitech.projects.icpms.persistence.entities.gate.Gate;
import org.flexitech.projects.icpms.persistence.entities.parking.ParkingArea;
import org.flexitech.projects.icpms.persistence.entities.site.Site;
import org.flexitech.projects.icpms.persistence.entities.user.User;
import org.flexitech.projects.icpms.persistence.repositories.gate.GateDeviceRepository;
import org.flexitech.projects.icpms.persistence.repositories.gate.GateRepository;
import org.flexitech.projects.icpms.persistence.repositories.parking.ParkingAreaRepository;
import org.flexitech.projects.icpms.persistence.repositories.site.SiteRepository;
import org.flexitech.projects.icpms.service.auth.AuthenticationService;
import org.flexitech.projects.icpms.service.specifications.gate.GateSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

@Service
public class GateServiceImpl implements GateService {

	private final GateRepository gateRepository;
	private final SiteRepository siteRepository;
	private final GateDeviceRepository gateDeviceRepository;
	private final ParkingAreaRepository parkingAreaRepository;
	private final AuthenticationService authenticationService;

	public GateServiceImpl(GateRepository gateRepository, SiteRepository siteRepository,
			GateDeviceRepository gateDeviceRepository, AuthenticationService authenticationService, ParkingAreaRepository parkingAreaRepository) {
		this.gateRepository = gateRepository;
		this.siteRepository = siteRepository;
		this.gateDeviceRepository = gateDeviceRepository;
		this.parkingAreaRepository = parkingAreaRepository;
		this.authenticationService = authenticationService;
	}

	@Override
	public GateDTO manageGate(GateDTO dto) throws Exception {
		Gate gate;
		User user = this.authenticationService.getLoggedInUser();
		if (CommonValidators.validLong(dto.getId())) {
			gate = this.gateRepository.findById(dto.getId())
					.orElseThrow(() -> new EntityNotFoundException("Gate doesn't exist!"));
			gate.setUpdatedTime(new Date());
			gate.setUpdatedBy(user);
		} else {
			gate = new Gate();
			gate.setCreatedTime(new Date());
			gate.setCreatedBy(user);
		}
		gate.setName(dto.getName());
		gate.setCode(dto.getCode());
		gate.setType(dto.getType());
		gate.setStatus(CommonValidators.isValidObject(dto.getStatus()) ? dto.getStatus() : ActiveStatus.ACTIVE.getCode());

		gate.setGateIpAddress(dto.getGateIpAddress());
		
		
		
		if (CommonValidators.validLong(dto.getSiteId())) {
			Site site = this.siteRepository.findById(dto.getSiteId())
					.orElseThrow(() -> new EntityNotFoundException("Site doesn't exist!"));
			gate.setSite(site);
		}
		
		if (CommonValidators.validLong(dto.getParkingAreaId())) {
			ParkingArea parkingArea = this.parkingAreaRepository.findById(dto.getSiteId())
					.orElseThrow(() -> new EntityNotFoundException("Parking area doesn't exist!"));
			gate.setParkingArea(parkingArea);
		}

		Gate saved = this.gateRepository.save(gate);
		return new GateDTO(saved);
	}

	@Override
	public GateDTO getGateById(Long id) throws Exception {
		Gate gate = this.gateRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Gate doesn't exist!"));
		return new GateDTO(gate);
	}

	@Override
	public SearchResultDTO<GateDTO> searchGates(GateSearchDTO searchDTO, Pageable pageable) throws Exception {
		Specification<Gate> spec = GateSpecification.withSearchCriteria(searchDTO);
		Page<Gate> page = gateRepository.findAll(spec, pageable);

		SearchResultDTO<GateDTO> result = new SearchResultDTO<>();
		result.setPageNo(page.getNumber());
		result.setLimit(page.getSize());
		result.setTotalPage(page.getTotalPages());
		result.setTotalRecords((int) page.getTotalElements());
		result.setPageCount(page.getNumberOfElements());
		result.setHasNextPage(page.hasNext());
		result.setResults(page.getContent().stream().map(GateDTO::new).collect(Collectors.toList()));
		return result;
	}

	@Override
	public List<GateDTO> findBySite(Long siteId) {
		return this.gateRepository.findBySiteId(siteId).stream().map(GateDTO::new).collect(Collectors.toList());
	}

	@Override
	public List<GateDTO> findAll() {
		return this.gateRepository.findAll().stream().map(GateDTO::new).collect(Collectors.toList());
	}

	@Override
	public boolean deleteGate(Long id) throws Exception {
		Gate gate = this.gateRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Gate doesn't exist!"));
		this.gateDeviceRepository.deleteByGateId(id);
		this.gateRepository.delete(gate);
		return true;
	}

	@Override
	public List<GateDTO> findAllActiveGates() {
		List<Gate> activeGates = this.gateRepository.findByStatus(ActiveStatus.ACTIVE.getCode());
		if(CommonValidators.validList(activeGates)) {
			return activeGates.stream().map(GateDTO::new).collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	@Override
	public GateDTO findByIpAddress(String ipAddress) {
		Gate gate = gateRepository.findByGateIpAddress(ipAddress)
				.orElseThrow(()-> new RuntimeException("Cannot find gate by this ip: "+ ipAddress));
		return new GateDTO(gate);
	}
}