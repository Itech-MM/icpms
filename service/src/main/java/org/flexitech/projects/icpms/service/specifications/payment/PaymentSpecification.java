package org.flexitech.projects.icpms.service.specifications.payment;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.icpms.common.CommonConstants;
import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.common.utils.DateUtils;
import org.flexitech.projects.icpms.dto.payment.PaymentSearchDTO;
import org.flexitech.projects.icpms.persistence.entities.payment.Payment;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

public class PaymentSpecification {
	public static Specification<Payment> withSearchCriteria(PaymentSearchDTO searchDTO) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (CommonValidators.validString(searchDTO.getPlateNumber())) {
				predicates.add(cb.like(cb.lower(root.get("session").get("vehicle").get("plateNumber")),
						"%" + searchDTO.getPlateNumber().toLowerCase() + "%"));
			}

			if (CommonValidators.isValidObject(searchDTO.getMethod())) {
				predicates.add(cb.equal(root.get("method"), searchDTO.getMethod()));
			}

			if (CommonValidators.isValidObject(searchDTO.getStatus())) {
				predicates.add(cb.equal(root.get("status"), searchDTO.getStatus()));
			}

			if (CommonValidators.validString(searchDTO.getFromDate())) {
				predicates.add(cb.greaterThanOrEqualTo(root.get("paymentTime"), DateUtils.stringToDate(
						searchDTO.getFromDate() + " " + CommonConstants.HOUR_START, CommonConstants.STD_YYYY_MM_DD_24)));
			}

			if (CommonValidators.validString(searchDTO.getToDate())) {
				predicates.add(cb.lessThanOrEqualTo(root.get("paymentTime"), DateUtils.stringToDate(
						searchDTO.getToDate() + " " + CommonConstants.HOUR_END, CommonConstants.STD_YYYY_MM_DD_24)));
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}
}
