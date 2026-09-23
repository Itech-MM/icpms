package org.flexitech.projects.icpms.service.specifications.operator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.flexitech.projects.icpms.common.CommonConstants;
import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.common.utils.DateUtils;
import org.flexitech.projects.icpms.dto.operator.OperatorShiftSearchDTO;
import org.flexitech.projects.icpms.persistence.entities.operator.OperatorShift;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

public class OperatorShiftSpecification {

	public static Specification<OperatorShift> withSearchCriteria(OperatorShiftSearchDTO searchDTO) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			
			if(CommonValidators.validLong(searchDTO.getOperatorId())) {
				predicates.add(cb.equal(root.get("operator").get("id"), searchDTO.getOperatorId()));
			}
			
			if(CommonValidators.validLong(searchDTO.getGateId())) {
				predicates.add(cb.equal(root.get("gate").get("id"), searchDTO.getGateId()));
			}
			
			if(CommonValidators.validString(searchDTO.getShiftCode())) {
				predicates.add(cb.equal(root.get("code"), searchDTO.getShiftCode()));
			}
			
			if(CommonValidators.validInteger(searchDTO.getShiftStatus())) {
				predicates.add(cb.equal(root.get("shiftStatus"), searchDTO.getShiftStatus()));
			}
			
			if(CommonValidators.validString(searchDTO.getFromDateTime())) {
				Date date = DateUtils.stringToDate(searchDTO.getFromDateTime(), CommonConstants.STANDARD_12_HOUR_DATE_MINUTE_FORMAT);
				predicates.add(cb.greaterThanOrEqualTo(root.get("createdTime"), date));
			}
			
			if(CommonValidators.validString(searchDTO.getToDateTime())) {
				Date date = DateUtils.stringToDate(searchDTO.getToDateTime(), CommonConstants.STANDARD_12_HOUR_DATE_MINUTE_FORMAT);
				predicates.add(cb.lessThanOrEqualTo(root.get("createdTime"), date));
			}
			
			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}
	
}
