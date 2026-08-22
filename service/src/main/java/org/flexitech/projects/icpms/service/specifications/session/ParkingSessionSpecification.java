package org.flexitech.projects.icpms.service.specifications.session;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.icpms.common.CommonConstants;
import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.common.utils.DateUtils;
import org.flexitech.projects.icpms.dto.session.ParkingSessionSearchDTO;
import org.flexitech.projects.icpms.persistence.entities.session.ParkingSession;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

public class ParkingSessionSpecification {
	public static Specification<ParkingSession> withSearchCriteria(ParkingSessionSearchDTO searchDTO) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (CommonValidators.validString(searchDTO.getPlateNumber())) {
				predicates.add(cb.like(cb.lower(root.get("vehicle").get("plateNumber")),
						"%" + searchDTO.getPlateNumber().toLowerCase() + "%"));
			}

			if (CommonValidators.validLong(searchDTO.getSiteId())) {
				predicates.add(cb.equal(root.get("entryGate").get("site").get("id"), searchDTO.getSiteId()));
			}

			if (CommonValidators.isValidObject(searchDTO.getStatus())) {
				predicates.add(cb.equal(root.get("status"), searchDTO.getStatus()));
			}

			if (CommonValidators.validString(searchDTO.getFromDate())) {
				predicates.add(cb.greaterThanOrEqualTo(root.get("entryTime"), DateUtils.stringToDate(
						searchDTO.getFromDate() + " " + CommonConstants.HOUR_START, CommonConstants.STD_YYYY_MM_DD_24)));
			}

			if (CommonValidators.validString(searchDTO.getToDate())) {
				predicates.add(cb.lessThanOrEqualTo(root.get("entryTime"), DateUtils.stringToDate(
						searchDTO.getToDate() + " " + CommonConstants.HOUR_END, CommonConstants.STD_YYYY_MM_DD_24)));
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}
}
