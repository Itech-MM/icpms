package org.flexitech.projects.icpms.service.specifications.user;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.dto.user.UserSearchDTO;
import org.flexitech.projects.icpms.persistence.entities.user.User;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

public class UserSpecification {
	public static Specification<User> withSearchCriteria(UserSearchDTO searchDTO) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (CommonValidators.validString(searchDTO.getName())) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")),
                    "%" + searchDTO.getName().toLowerCase() + "%"
                ));
            }
            
            if (CommonValidators.validString(searchDTO.getPhoneNumber())) {
                predicates.add(criteriaBuilder.like(
                    root.get("phoneNumber"),
                    "%" + searchDTO.getPhoneNumber() + "%"
                ));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
