package org.flexitech.projects.icpms.service.parking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.common.enums.ActiveStatus;
import org.flexitech.projects.icpms.dto.SearchResultDTO;
import org.flexitech.projects.icpms.dto.parking.ParkingAreaDTO;
import org.flexitech.projects.icpms.dto.parking.ParkingAreaRealtimeDTO;
import org.flexitech.projects.icpms.dto.parking.ParkingAreaSearchDTO;
import org.flexitech.projects.icpms.dto.slot.ParkingSlotDTO;
import org.flexitech.projects.icpms.persistence.entities.gate.Gate;
import org.flexitech.projects.icpms.persistence.entities.parking.ParkingArea;
import org.flexitech.projects.icpms.persistence.entities.slot.ParkingSlot;
import org.flexitech.projects.icpms.persistence.entities.tariff.Tariff;
import org.flexitech.projects.icpms.persistence.repositories.gate.GateRepository;
import org.flexitech.projects.icpms.persistence.repositories.parking.ParkingAreaRepository;
import org.flexitech.projects.icpms.persistence.repositories.slot.ParkingSlotRepository;
import org.flexitech.projects.icpms.persistence.repositories.tariff.TariffRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

@Service
@Transactional(readOnly = true)
public class ParkingAreaServiceImpl implements ParkingAreaService {

	private final ParkingAreaRepository parkingAreaRepository;
	private final TariffRepository tariffRepository;
	private final ParkingSlotRepository parkingSlotRepository;
	private final GateRepository gateRepository;

	public ParkingAreaServiceImpl(ParkingAreaRepository parkingAreaRepository, TariffRepository tariffRepository, ParkingSlotRepository parkingSlotRepository, GateRepository gateRepository) {
		this.parkingAreaRepository = parkingAreaRepository;
		this.tariffRepository = tariffRepository;
		this.parkingSlotRepository = parkingSlotRepository;
		this.gateRepository = gateRepository;
	}

	@Override
	public SearchResultDTO<ParkingAreaDTO> searchParkingAreas(ParkingAreaSearchDTO searchDTO, Pageable pageable) throws Exception {
		Specification<ParkingArea> spec = buildSpecification(searchDTO);
		Page<ParkingArea> page = parkingAreaRepository.findAll(spec, pageable);

		List<ParkingAreaDTO> dtoList = new ArrayList<>();
		for (ParkingArea entity : page.getContent()) {
			dtoList.add(new ParkingAreaDTO(entity));
		}

		SearchResultDTO<ParkingAreaDTO> result = new SearchResultDTO<>();
		result.setResults(dtoList);
		result.setPageNo(page.getNumber());
		result.setLimit(pageable.getPageSize());
		result.setTotalPage(page.getTotalPages());
		result.setTotalRecords((int) page.getTotalElements());
		result.setPageCount(dtoList.size());
		result.setHasNextPage(page.hasNext());
		return result;
	}

	private Specification<ParkingArea> buildSpecification(ParkingAreaSearchDTO searchDTO) {
		List<Specification<ParkingArea>> specs = new ArrayList<>();

		if (searchDTO.getName() != null && !searchDTO.getName().isBlank()) {
			specs.add((root, query, cb) ->
					cb.like(cb.lower(root.get("name")), "%" + searchDTO.getName().toLowerCase() + "%"));
		}

		if (searchDTO.getGateId() != null) {
			specs.add((root, query, cb) -> {
				Subquery<Long> subquery = query.subquery(Long.class);
				Root<Gate> gateRoot = subquery.from(Gate.class);
				subquery.select(gateRoot.get("parkingArea").get("id"))
						.where(cb.equal(gateRoot.get("id"), searchDTO.getGateId()));
				return cb.equal(root.get("id"), subquery);
			});
		}

		if (searchDTO.getStatus() != null) {
			specs.add((root, query, cb) ->
					cb.equal(root.get("status"), searchDTO.getStatus()));
		}

		return Specification.allOf(specs);
	}
	@Override
	public ParkingAreaDTO getParkingAreaById(Long id) throws Exception {
		ParkingArea entity = parkingAreaRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Parking area not found: " + id));
		return new ParkingAreaDTO(entity);
	}

	@Override
	@Transactional
	public void manageParkingArea(ParkingAreaDTO dto) throws Exception {
		ParkingArea entity;
		if (dto.getId() != null) {
			entity = parkingAreaRepository.findById(dto.getId())
					.orElseThrow(() -> new EntityNotFoundException("Parking area not found: " + dto.getId()));
		} else {
			entity = new ParkingArea();
		}

		entity.setName(dto.getName());
		entity.setRemark(dto.getRemark());
		entity.setTotalSlot(dto.getTotalSlot());
		entity.setVipSlot(dto.getVipSlot());
		entity.setAvailableTotalSlot(dto.getAvailableTotalSlot());
		entity.setAvailableTotalVipSlot(dto.getAvailableTotalVipSlot());
		entity.setSlotTrackingEnabled(dto.getSlotTrackingEnabled());
		entity.setStatus(dto.getStatus());

		if(CommonValidators.validLong(dto.getTariffId())) {
			Tariff t = this.tariffRepository.findById(dto.getTariffId())
					.orElseThrow(() -> new EntityNotFoundException("Tariff doesn't exist!"));
			entity.setTariff(t);
		}
		
		parkingAreaRepository.save(entity);
	}

	@Override
	@Transactional
	public void deleteParkingArea(Long id) throws Exception {
		if (!parkingAreaRepository.existsById(id)) {
			throw new EntityNotFoundException("Parking area not found: " + id);
		}
		parkingAreaRepository.deleteById(id);
	}

	@Override
	public List<ParkingAreaDTO> findAllActiveParkingAreas() {
		List<ParkingArea> activeList = this.parkingAreaRepository.findByStatus(ActiveStatus.ACTIVE.getCode());
		
		if(CommonValidators.validList(activeList))
			return activeList.stream().map(ParkingAreaDTO::new).collect(Collectors.toList());
		
		return Collections.emptyList();
	}

	@Override
	public ParkingAreaRealtimeDTO getRealtimeByGate(String gateIpAddress) throws Exception {
		Gate gate = this.gateRepository.findByGateIpAddress(gateIpAddress)
				.orElseThrow(() -> new EntityNotFoundException("Gate doesn't exist!"));

		ParkingArea parkingArea = gate.getParkingArea();
		if (parkingArea == null) {
			throw new EntityNotFoundException("Gate is not linked to a parking area!");
		}

		ParkingAreaRealtimeDTO dto = new ParkingAreaRealtimeDTO(parkingArea);

		if (Boolean.TRUE.equals(parkingArea.getSlotTrackingEnabled())) {
			List<ParkingSlot> slots = this.parkingSlotRepository.findByParkingAreaId(parkingArea.getId());
			dto.setSlots(slots.stream().map(ParkingSlotDTO::new).collect(Collectors.toList()));
		}

		return dto;
	}

	@Override
	public ParkingAreaDTO getByGateId(Long gateId) {
		Gate gate = this.gateRepository.findById(gateId)
				.orElseThrow(()-> new RuntimeException("Invalid gate."));
		
		return new ParkingAreaDTO(gate.getParkingArea());
	}

}