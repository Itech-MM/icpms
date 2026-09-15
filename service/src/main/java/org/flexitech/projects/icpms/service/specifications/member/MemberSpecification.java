package org.flexitech.projects.icpms.service.specifications.member;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.common.enums.SubscriptionStatus;
import org.flexitech.projects.icpms.dto.member.MemberSearchDTO;
import org.flexitech.projects.icpms.persistence.entities.member.Member;
import org.flexitech.projects.icpms.persistence.entities.member.MemberSubscription;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public class MemberSpecification {
	public static Specification<Member> withSearchCriteria(MemberSearchDTO searchDTO) {
	    return (root, query, cb) -> {
	        List<Predicate> predicates = new ArrayList<>();

	        if (CommonValidators.validString(searchDTO.getKeyword())) {
	            String keywordLower = "%" + searchDTO.getKeyword().toLowerCase() + "%";
	            String keywordRaw = "%" + searchDTO.getKeyword() + "%";

	            Predicate nameMatch = cb.like(cb.lower(root.get("name")), keywordLower);
	            Predicate phoneMatch = cb.like(root.get("phoneNumber"), keywordRaw);

	            predicates.add(cb.or(nameMatch, phoneMatch));
	        }

	        if (CommonValidators.validString(searchDTO.getName())) {
	            predicates.add(cb.like(cb.lower(root.get("name")), "%" + searchDTO.getName().toLowerCase() + "%"));
	        }
	        if (CommonValidators.validString(searchDTO.getPhoneNumber())) {
	            predicates.add(cb.like(root.get("phoneNumber"), "%" + searchDTO.getPhoneNumber() + "%"));
	        }
	        if (CommonValidators.isValidObject(searchDTO.getStatus())) {
	            predicates.add(cb.equal(root.get("status"), searchDTO.getStatus()));
	        }

	        return cb.and(predicates.toArray(new Predicate[0]));
	    };
	}

	public static Specification<Member> isVip() {
		return (root, query, cb) -> cb.isTrue(root.get("isVip"));
	}

	public static Specification<Member> isRegular() {
		return Specification.not(isVip());
	}

	public static Specification<Member> isExpired() {
		return (root, query, cb) -> {
			Join<Member, MemberSubscription> subscription = root.join("currentSubscription", JoinType.LEFT);
			Predicate noSubscription = cb.isNull(root.get("currentSubscription"));
			Predicate notActiveStatus = cb.notEqual(subscription.get("status"), SubscriptionStatus.ACTIVE.getCode());
			Predicate endDatePassed = cb.and(cb.isNotNull(subscription.get("endDate")), cb.lessThan(subscription.get("endDate"), new Date()));
			return cb.or(noSubscription, notActiveStatus, endDatePassed);
		};
	}

	public static Specification<Member> isActive() {
		return Specification.not(isExpired());
	}
}