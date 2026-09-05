package org.flexitech.projects.icpms.service.specifications.audit_logs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.flexitech.projects.icpms.common.CommonConstants;
import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.common.utils.DateUtils;
import org.flexitech.projects.icpms.dto.audit_logs.VehicleAlertLogSearchDTO;
import org.flexitech.projects.icpms.persistence.entities.audit_logs.VehicleAlertLog;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

public class VehicleAlertLogSpecification {
	public static Specification<VehicleAlertLog> withSearchCriteria(VehicleAlertLogSearchDTO searchDTO) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (CommonValidators.validString(searchDTO.getStartDate())) {
				Date date = DateUtils.stringToDate(searchDTO.getStartDate(), CommonConstants.STANDARD_24_HOUR_DATE_FORMAT2);
				predicates.add(cb.greaterThanOrEqualTo(root.get("createdTime"), date));
			}
			if (CommonValidators.validString(searchDTO.getEndDate())) {
				Date date = DateUtils.stringToDate(searchDTO.getEndDate(), CommonConstants.STANDARD_24_HOUR_DATE_FORMAT2);
				predicates.add(cb.lessThanOrEqualTo(root.get("createdTime"), date));
			}
			if (CommonValidators.validLong(searchDTO.getSessionId())) {
				predicates.add(cb.equal(root.get("sessionId"), searchDTO.getSessionId()));
			}
			if (CommonValidators.validLong(searchDTO.getGateId())) {
				predicates.add(cb.equal(root.get("gateId"), searchDTO.getGateId()));
			}
			if (CommonValidators.validInteger(searchDTO.getStatus())) {
				predicates.add(cb.equal(root.get("status"), searchDTO.getGateId()));
			}
			
			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}
}
