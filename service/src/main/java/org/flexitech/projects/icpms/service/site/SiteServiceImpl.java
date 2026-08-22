package org.flexitech.projects.icpms.service.site;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.common.enums.ActiveStatus;
import org.flexitech.projects.icpms.dto.SearchResultDTO;
import org.flexitech.projects.icpms.dto.site.SiteDTO;
import org.flexitech.projects.icpms.dto.site.SiteSearchDTO;
import org.flexitech.projects.icpms.persistence.entities.site.Site;
import org.flexitech.projects.icpms.persistence.entities.user.User;
import org.flexitech.projects.icpms.persistence.repositories.site.SiteRepository;
import org.flexitech.projects.icpms.service.auth.AuthenticationService;
import org.flexitech.projects.icpms.service.specifications.site.SiteSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

@Service
public class SiteServiceImpl implements SiteService {

	private final SiteRepository siteRepository;
	private final AuthenticationService authenticationService;

	public SiteServiceImpl(SiteRepository siteRepository, AuthenticationService authenticationService) {
		this.siteRepository = siteRepository;
		this.authenticationService = authenticationService;
	}

	@Override
	public SiteDTO manageSite(SiteDTO dto) throws Exception {
		Site site;
		User user = this.authenticationService.getLoggedInUser();
		if (CommonValidators.validLong(dto.getId())) {
			site = this.siteRepository.findById(dto.getId())
					.orElseThrow(() -> new EntityNotFoundException("Site doesn't exist!"));
			site.setUpdatedTime(new Date());
			site.setUpdatedBy(user);
		} else {
			site = new Site();
			site.setCreatedTime(new Date());
			site.setCreatedBy(user);
		}
		site.setName(dto.getName());
		site.setCode(dto.getCode());
		site.setAddress(dto.getAddress());
		site.setTotalCapacity(dto.getTotalCapacity());
		site.setStatus(CommonValidators.isValidObject(dto.getStatus()) ? dto.getStatus() : ActiveStatus.ACTIVE.getCode());

		Site saved = this.siteRepository.save(site);
		return new SiteDTO(saved);
	}

	@Override
	public SiteDTO getSiteById(Long id) throws Exception {
		Site site = this.siteRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Site doesn't exist!"));
		return new SiteDTO(site);
	}

	@Override
	public SearchResultDTO<SiteDTO> searchSites(SiteSearchDTO searchDTO, Pageable pageable) throws Exception {
		Specification<Site> spec = SiteSpecification.withSearchCriteria(searchDTO);
		Page<Site> page = siteRepository.findAll(spec, pageable);

		SearchResultDTO<SiteDTO> result = new SearchResultDTO<>();
		result.setPageNo(page.getNumber());
		result.setLimit(page.getSize());
		result.setTotalPage(page.getTotalPages());
		result.setTotalRecords((int) page.getTotalElements());
		result.setPageCount(page.getNumberOfElements());
		result.setHasNextPage(page.hasNext());
		result.setResults(page.getContent().stream().map(SiteDTO::new).collect(Collectors.toList()));
		return result;
	}

	@Override
	public List<SiteDTO> findAllActiveSites() {
		return this.siteRepository.findAll(SiteSpecification.withSearchCriteria(activeOnly()))
				.stream().map(SiteDTO::new).collect(Collectors.toList());
	}

	private SiteSearchDTO activeOnly() {
		SiteSearchDTO dto = new SiteSearchDTO();
		dto.setStatus(ActiveStatus.ACTIVE.getCode());
		return dto;
	}

	@Override
	public boolean deleteSite(Long id) throws Exception {
		Site site = this.siteRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Site doesn't exist!"));
		this.siteRepository.delete(site);
		return true;
	}
}
