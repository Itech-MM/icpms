package org.flexitech.projects.icpms.service.specifications.site;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.dto.site.SiteSearchDTO;
import org.flexitech.projects.icpms.persistence.entities.site.Site;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

public class SiteSpecification {
	public static Specification<Site> withSearchCriteria(SiteSearchDTO searchDTO) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (CommonValidators.validString(searchDTO.getName())) {
				predicates.add(cb.like(cb.lower(root.get("name")), "%" + searchDTO.getName().toLowerCase() + "%"));
			}
			if (CommonValidators.isValidObject(searchDTO.getStatus())) {
				predicates.add(cb.equal(root.get("status"), searchDTO.getStatus()));
			}
			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}
}
