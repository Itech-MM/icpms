package org.flexitech.projects.icpms.service.specifications.slot;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.dto.slot.ParkingSlotSearchDTO;
import org.flexitech.projects.icpms.persistence.entities.slot.ParkingSlot;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

public class ParkingSlotSpecification {
	public static Specification<ParkingSlot> withSearchCriteria(ParkingSlotSearchDTO searchDTO) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (CommonValidators.validString(searchDTO.getSlotNumber())) {
				predicates.add(cb.like(cb.lower(root.get("slotNumber")), "%" + searchDTO.getSlotNumber().toLowerCase() + "%"));
			}
			if (CommonValidators.validLong(searchDTO.getSiteId())) {
				predicates.add(cb.equal(root.get("site").get("id"), searchDTO.getSiteId()));
			}
			if (CommonValidators.isValidObject(searchDTO.getIsVip())) {
				predicates.add(cb.equal(root.get("isVip"), searchDTO.getIsVip()));
			}
			if (CommonValidators.isValidObject(searchDTO.getStatus())) {
				predicates.add(cb.equal(root.get("status"), searchDTO.getStatus()));
			}
			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}
}
