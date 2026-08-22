package org.flexitech.projects.icpms.service.specifications.gate;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.dto.gate.GateSearchDTO;
import org.flexitech.projects.icpms.persistence.entities.gate.Gate;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

public class GateSpecification {
	public static Specification<Gate> withSearchCriteria(GateSearchDTO searchDTO) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (CommonValidators.validString(searchDTO.getName())) {
				predicates.add(cb.like(cb.lower(root.get("name")), "%" + searchDTO.getName().toLowerCase() + "%"));
			}
			if (CommonValidators.validLong(searchDTO.getSiteId())) {
				predicates.add(cb.equal(root.get("site").get("id"), searchDTO.getSiteId()));
			}
			if (CommonValidators.isValidObject(searchDTO.getStatus())) {
				predicates.add(cb.equal(root.get("status"), searchDTO.getStatus()));
			}
			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}
}
