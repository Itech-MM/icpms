package org.flexitech.projects.icpms.service.member;

import java.util.List;
import java.util.stream.Collectors;

import org.flexitech.projects.icpms.dto.member.MemberBalanceTransactionDTO;
import org.flexitech.projects.icpms.persistence.repositories.member.MemberBalanceTransactionRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberBalanceTransactionServiceImpl implements MemberBalanceTransactionService {

	private final MemberBalanceTransactionRepository memberBalanceTransactionRepository;

	@Override
	public List<MemberBalanceTransactionDTO> getTransactionsBySubscription(Long subscriptionId) {
		return memberBalanceTransactionRepository.findBySubscriptionIdOrderByCreatedTimeDesc(subscriptionId)
				.stream()
				.map(MemberBalanceTransactionDTO::new)
				.collect(Collectors.toList());
	}
}