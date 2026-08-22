package org.flexitech.projects.icpms.service.site;

import org.flexitech.projects.icpms.dto.SearchResultDTO;
import org.flexitech.projects.icpms.dto.site.SiteDTO;
import org.flexitech.projects.icpms.dto.site.SiteSearchDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SiteService {
	SiteDTO manageSite(SiteDTO dto) throws Exception;
	SiteDTO getSiteById(Long id) throws Exception;
	SearchResultDTO<SiteDTO> searchSites(SiteSearchDTO searchDTO, Pageable pageable) throws Exception;
	List<SiteDTO> findAllActiveSites();
	boolean deleteSite(Long id) throws Exception;
}
