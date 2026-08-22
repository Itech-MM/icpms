package org.flexitech.projects.icpms.service.slot;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.common.enums.SlotStatus;
import org.flexitech.projects.icpms.dto.SearchResultDTO;
import org.flexitech.projects.icpms.dto.slot.ParkingSlotBulkDTO;
import org.flexitech.projects.icpms.dto.slot.ParkingSlotDTO;
import org.flexitech.projects.icpms.dto.slot.ParkingSlotSearchDTO;
import org.flexitech.projects.icpms.persistence.entities.site.Site;
import org.flexitech.projects.icpms.persistence.entities.slot.ParkingSlot;
import org.flexitech.projects.icpms.persistence.entities.user.User;
import org.flexitech.projects.icpms.persistence.repositories.site.SiteRepository;
import org.flexitech.projects.icpms.persistence.repositories.slot.ParkingSlotRepository;
import org.flexitech.projects.icpms.service.auth.AuthenticationService;
import org.flexitech.projects.icpms.service.specifications.slot.ParkingSlotSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ParkingSlotServiceImpl implements ParkingSlotService {

	private final ParkingSlotRepository slotRepository;
	private final SiteRepository siteRepository;
	private final AuthenticationService authenticationService;

	public ParkingSlotServiceImpl(ParkingSlotRepository slotRepository, SiteRepository siteRepository,
			AuthenticationService authenticationService) {
		this.slotRepository = slotRepository;
		this.siteRepository = siteRepository;
		this.authenticationService = authenticationService;
	}

	@Override
	public ParkingSlotDTO manageSlot(ParkingSlotDTO dto) throws Exception {
		ParkingSlot slot;
		User user = this.authenticationService.getLoggedInUser();
		if (CommonValidators.validLong(dto.getId())) {
			slot = this.slotRepository.findById(dto.getId())
					.orElseThrow(() -> new EntityNotFoundException("Parking slot doesn't exist!"));
			slot.setUpdatedTime(new Date());
			slot.setUpdatedBy(user);
		} else {
			slot = new ParkingSlot();
			slot.setCreatedTime(new Date());
			slot.setCreatedBy(user);
		}
		slot.setSlotNumber(dto.getSlotNumber());
		slot.setFloorLevel(dto.getFloorLevel());
		slot.setIsVip(dto.getIsVip() != null && dto.getIsVip());
		slot.setStatus(CommonValidators.isValidObject(dto.getStatus()) ? dto.getStatus() : SlotStatus.AVAILABLE.getCode());

		if (CommonValidators.validLong(dto.getSiteId())) {
			Site site = this.siteRepository.findById(dto.getSiteId())
					.orElseThrow(() -> new EntityNotFoundException("Site doesn't exist!"));
			slot.setSite(site);
		}

		ParkingSlot saved = this.slotRepository.save(slot);
		return new ParkingSlotDTO(saved);
	}

	@Override
	public ParkingSlotDTO getSlotById(Long id) throws Exception {
		ParkingSlot slot = this.slotRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Parking slot doesn't exist!"));
		return new ParkingSlotDTO(slot);
	}

	@Override
	public SearchResultDTO<ParkingSlotDTO> searchSlots(ParkingSlotSearchDTO searchDTO, Pageable pageable) throws Exception {
		Specification<ParkingSlot> spec = ParkingSlotSpecification.withSearchCriteria(searchDTO);
		Page<ParkingSlot> page = slotRepository.findAll(spec, pageable);

		SearchResultDTO<ParkingSlotDTO> result = new SearchResultDTO<>();
		result.setPageNo(page.getNumber());
		result.setLimit(page.getSize());
		result.setTotalPage(page.getTotalPages());
		result.setTotalRecords((int) page.getTotalElements());
		result.setPageCount(page.getNumberOfElements());
		result.setHasNextPage(page.hasNext());
		result.setResults(page.getContent().stream().map(ParkingSlotDTO::new).collect(Collectors.toList()));
		return result;
	}

	@Override
	public List<ParkingSlotDTO> findAvailableSlots(Long siteId) {
		ParkingSlotSearchDTO searchDTO = new ParkingSlotSearchDTO();
		searchDTO.setSiteId(siteId);
		searchDTO.setStatus(SlotStatus.AVAILABLE.getCode());
		Specification<ParkingSlot> spec = ParkingSlotSpecification.withSearchCriteria(searchDTO);
		return this.slotRepository.findAll(spec).stream().map(ParkingSlotDTO::new).collect(Collectors.toList());
	}

	@Override
	public boolean deleteSlot(Long id) throws Exception {
		ParkingSlot slot = this.slotRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Parking slot doesn't exist!"));
		this.slotRepository.delete(slot);
		return true;
	}
	
	@Transactional
	@Override
	public int bulkCreateSlots(ParkingSlotBulkDTO bulkDTO) throws Exception {
	    String prefix = bulkDTO.getPrefix() != null ? bulkDTO.getPrefix() : "";
	    int count = 0;
	    for (int i = bulkDTO.getFromNumber(); i <= bulkDTO.getToNumber(); i++) {
	        ParkingSlotDTO dto = new ParkingSlotDTO();
	        dto.setSiteId(bulkDTO.getSiteId());
	        dto.setSlotNumber(prefix + i);
	        dto.setFloorLevel(bulkDTO.getFloorLevel());
	        dto.setStatus(bulkDTO.getStatus());
	        dto.setIsVip(bulkDTO.getIsVip());
	        manageSlot(dto);
	        count++;
	    }
	    return count;
	}
}
