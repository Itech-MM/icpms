package org.flexitech.projects.icpms.service.specifications.gate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.icpms.common.CommonConstants;
import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.common.utils.DateUtils;
import org.flexitech.projects.icpms.dto.gate.GateDeviceDiagnosisSearchDTO;
import org.flexitech.projects.icpms.persistence.entities.gate.GateDeviceDiagnosis;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

public class GateDeviceDiagnosisSpecification {
	public static Specification<GateDeviceDiagnosis> withSearchCriteria(GateDeviceDiagnosisSearchDTO searchDTO) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (CommonValidators.validLong(searchDTO.getGateDeviceId())) {
				predicates.add(cb.equal(root.get("gateDevice").get("id"), searchDTO.getGateDeviceId()));
			}

			if (CommonValidators.validString(searchDTO.getFromDate())) {
				LocalDateTime from = DateUtils.stringToLocalDateTime(searchDTO.getFromDate(),
						CommonConstants.STANDARD_12_HOUR_DATE_MINUTE_FORMAT);
				if (from != null) {
					predicates.add(cb.greaterThanOrEqualTo(root.get("checkedAt"), from));
				}
			}

			if (CommonValidators.validString(searchDTO.getToDate())) {
				LocalDateTime to = DateUtils.stringToLocalDateTime(searchDTO.getToDate(),
						CommonConstants.STANDARD_12_HOUR_DATE_MINUTE_FORMAT);
				if (to != null) {
					predicates.add(cb.lessThanOrEqualTo(root.get("checkedAt"), to));
				}
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}
}
