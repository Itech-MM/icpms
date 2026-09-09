package org.flexitech.projects.icpms.service.specifications.vehicle;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.common.enums.ParkingSessionStatus;
import org.flexitech.projects.icpms.dto.vehicle.VehicleSearchDTO;
import org.flexitech.projects.icpms.persistence.entities.session.ParkingSession;
import org.flexitech.projects.icpms.persistence.entities.vehicle.Vehicle;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

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

			if (CommonValidators.isValidObject(searchDTO.getFromSession())) {
				Subquery<Long> subquery = query.subquery(Long.class);
				Root<ParkingSession> sessionRoot = subquery.from(ParkingSession.class);
				subquery.select(sessionRoot.get("id"));

				List<Predicate> sessionPredicates = new ArrayList<>();
				sessionPredicates.add(cb.equal(sessionRoot.get("vehicle"), root));
				sessionPredicates.add(cb.equal(sessionRoot.get("status"), ParkingSessionStatus.ACTIVE.getCode()));

				if (CommonValidators.isValidObject(searchDTO.getParkingAreaId())) {
					sessionPredicates.add(cb.equal(sessionRoot.get("parkingArea").get("id"), searchDTO.getParkingAreaId()));
				}

				subquery.where(sessionPredicates.toArray(new Predicate[0]));

				if (searchDTO.getFromSession() == 1) {
					predicates.add(cb.exists(subquery));
				} else if (searchDTO.getFromSession() == 2) {
					predicates.add(cb.not(cb.exists(subquery)));
				}
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}
}
