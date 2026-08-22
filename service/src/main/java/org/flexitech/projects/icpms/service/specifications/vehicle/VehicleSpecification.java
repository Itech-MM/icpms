package org.flexitech.projects.icpms.service.specifications.vehicle;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.dto.vehicle.VehicleSearchDTO;
import org.flexitech.projects.icpms.persistence.entities.vehicle.Vehicle;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

public class VehicleSpecification {
	public static Specification<Vehicle> withSearchCriteria(VehicleSearchDTO searchDTO) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (CommonValidators.validString(searchDTO.getPlateNumber())) {
				predicates.add(cb.like(cb.lower(root.get("plateNumber")), "%" + searchDTO.getPlateNumber().toLowerCase() + "%"));
			}
			if (CommonValidators.isValidObject(searchDTO.getStatus())) {
				predicates.add(cb.equal(root.get("status"), searchDTO.getStatus()));
			}
			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}
}
