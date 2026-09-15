package org.flexitech.projects.icpms.service.member;

import org.flexitech.projects.icpms.dto.api.request.member.ChargeMemberSessionRequest;
import org.flexitech.projects.icpms.dto.api.request.member.ChargeResultDTO;
import org.flexitech.projects.icpms.dto.api.request.member.SubscribeMemberPlanRequest;
import org.flexitech.projects.icpms.dto.api.request.member.TopUpBalanceRequest;
import org.flexitech.projects.icpms.dto.member.MemberSubscriptionDTO;

public interface MemberSubscriptionService {

	MemberSubscriptionDTO subscribe(SubscribeMemberPlanRequest request);

	MemberSubscriptionDTO renew(Long subscriptionId, Long supervisorId);

	MemberSubscriptionDTO topUp(Long subscriptionId, TopUpBalanceRequest request);

	MemberSubscriptionDTO cancel(Long subscriptionId, Long supervisorId);

	MemberSubscriptionDTO getActiveSubscription(Long memberId);

	ChargeResultDTO chargeSessionFee(ChargeMemberSessionRequest request);

	void expireOverdueSubscriptions();
}