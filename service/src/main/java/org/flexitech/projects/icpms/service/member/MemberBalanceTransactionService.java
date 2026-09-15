package org.flexitech.projects.icpms.service.member;

import java.util.List;

import org.flexitech.projects.icpms.dto.member.MemberBalanceTransactionDTO;

public interface MemberBalanceTransactionService {

	List<MemberBalanceTransactionDTO> getTransactionsBySubscription(Long subscriptionId);
}