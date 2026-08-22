package org.flexitech.projects.icpms.persistence.repositories.site;

import org.flexitech.projects.icpms.persistence.entities.site.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SiteRepository extends JpaRepository<Site, Long>, JpaSpecificationExecutor<Site> {
	boolean existsByCodeIgnoreCase(String code);
}
