package org.flexitech.projects.icpms.service.member;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.common.enums.BalanceTransactionType;
import org.flexitech.projects.icpms.common.enums.SubscriptionStatus;
import org.flexitech.projects.icpms.common.exceptions.MemberAlreadySubscribedException;
import org.flexitech.projects.icpms.common.exceptions.MemberPlanNotFoundException;
import org.flexitech.projects.icpms.common.exceptions.NoActiveSubscriptionException;
import org.flexitech.projects.icpms.dto.api.request.member.ChargeMemberSessionRequest;
import org.flexitech.projects.icpms.dto.api.request.member.ChargeResultDTO;
import org.flexitech.projects.icpms.dto.api.request.member.SubscribeMemberPlanRequest;
import org.flexitech.projects.icpms.dto.api.request.member.TopUpBalanceRequest;
import org.flexitech.projects.icpms.dto.member.MemberSubscriptionDTO;
import org.flexitech.projects.icpms.persistence.entities.member.Member;
import org.flexitech.projects.icpms.persistence.entities.member.MemberBalanceTransaction;
import org.flexitech.projects.icpms.persistence.entities.member.MemberPlan;
import org.flexitech.projects.icpms.persistence.entities.member.MemberSubscription;
import org.flexitech.projects.icpms.persistence.entities.operator.Operator;
import org.flexitech.projects.icpms.persistence.entities.session.ParkingSession;
import org.flexitech.projects.icpms.persistence.repositories.member.MemberBalanceTransactionRepository;
import org.flexitech.projects.icpms.persistence.repositories.member.MemberPlanRepository;
import org.flexitech.projects.icpms.persistence.repositories.member.MemberRepository;
import org.flexitech.projects.icpms.persistence.repositories.member.MemberSubscriptionRepository;
import org.flexitech.projects.icpms.persistence.repositories.operator.OperatorRepository;
import org.flexitech.projects.icpms.persistence.repositories.session.ParkingSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MemberSubscriptionServiceImpl implements MemberSubscriptionService {

	@Autowired
	private MemberSubscriptionRepository memberSubscriptionRepository;

	@Autowired
	private MemberPlanRepository memberPlanRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private MemberBalanceTransactionRepository memberBalanceTransactionRepository;

	@Autowired
	private ParkingSessionRepository parkingSessionRepository;
	
	@Autowired
	private OperatorRepository operatorRepository;

	@Override
	@Transactional
	public MemberSubscriptionDTO subscribe(SubscribeMemberPlanRequest request) {
		Member member = memberRepository.findById(request.getMemberId())
				.orElseThrow(() -> new IllegalArgumentException("Member not found: " + request.getMemberId()));

		if (member.getCurrentSubscription() != null
				&& member.getCurrentSubscription().getStatus() == SubscriptionStatus.ACTIVE.getCode()
				&& member.getCurrentSubscription().getEndDate() != null
				&& member.getCurrentSubscription().getEndDate().after(new Date())) {
			throw new MemberAlreadySubscribedException("Member already has an active subscription, use renew instead");
		}

		MemberPlan plan = memberPlanRepository.findById(request.getPlanId())
				.filter(p -> Boolean.TRUE.equals(p.getIsActive()))
				.orElseThrow(() -> new MemberPlanNotFoundException("Active member plan not found: " + request.getPlanId()));

		Date now = new Date();
		Date endDate = addDays(now, plan.getDurationDays());
		BigDecimal grantedBalance = plan.getGrantedBalance() != null ? plan.getGrantedBalance() : BigDecimal.ZERO;

		MemberSubscription subscription = new MemberSubscription();
		subscription.setMember(member);
		subscription.setPlan(plan);
		subscription.setStartDate(now);
		subscription.setEndDate(endDate);
		subscription.setInitialBalance(grantedBalance);
		subscription.setBalance(grantedBalance);
		subscription.setStatus(SubscriptionStatus.ACTIVE.getCode());
		subscription.setSupervisor(operatorRepository.findById(request.getSupervisorId()).orElse(null));
		subscription = memberSubscriptionRepository.save(subscription);

		member.setCurrentSubscription(subscription);
		memberRepository.save(member);

		recordTransaction(subscription, member, null, BalanceTransactionType.CREDIT, grantedBalance, subscription.getBalance(),
				"Subscription created: " + plan.getCode(), request.getSupervisorId());

		return new MemberSubscriptionDTO(subscription);
	}

	@Override
	@Transactional
	public MemberSubscriptionDTO renew(Long subscriptionId, Long supervisorId) {
		MemberSubscription subscription = memberSubscriptionRepository.findById(subscriptionId)
				.orElseThrow(() -> new NoActiveSubscriptionException("Subscription not found: " + subscriptionId));

		MemberPlan plan = subscription.getPlan();
		Date base = (subscription.getEndDate() != null && subscription.getEndDate().after(new Date()))
				? subscription.getEndDate() : new Date();
		subscription.setEndDate(addDays(base, plan.getDurationDays()));
		subscription.setStatus(SubscriptionStatus.ACTIVE.getCode());
		memberSubscriptionRepository.save(subscription);

		BigDecimal grantedBalance = plan.getGrantedBalance() != null ? plan.getGrantedBalance() : BigDecimal.ZERO;
		memberSubscriptionRepository.creditBalance(subscription.getId(), grantedBalance);

		MemberSubscription refreshed = memberSubscriptionRepository.findById(subscription.getId())
				.orElseThrow(() -> new NoActiveSubscriptionException("Subscription not found: " + subscriptionId));

		Member member = refreshed.getMember();
		if (member.getCurrentSubscription() == null) {
			member.setCurrentSubscription(refreshed);
			memberRepository.save(member);
		}

		recordTransaction(refreshed, member, null, BalanceTransactionType.CREDIT, grantedBalance, refreshed.getBalance(),
				"Subscription renewed: " + plan.getCode(), supervisorId);

		return new MemberSubscriptionDTO(refreshed);
	}

	@Override
	@Transactional
	public MemberSubscriptionDTO topUp(Long subscriptionId, TopUpBalanceRequest request) {
		memberSubscriptionRepository.creditBalance(subscriptionId, request.getAmount());
		MemberSubscription refreshed = memberSubscriptionRepository.findById(subscriptionId)
				.orElseThrow(() -> new NoActiveSubscriptionException("Subscription not found: " + subscriptionId));

		recordTransaction(refreshed, refreshed.getMember(), null, BalanceTransactionType.CREDIT, request.getAmount(),
				refreshed.getBalance(), request.getRemark(), request.getSupervisorId());

		return new MemberSubscriptionDTO(refreshed);
	}

	@Override
	@Transactional
	public MemberSubscriptionDTO cancel(Long subscriptionId, Long supervisorId) {
		MemberSubscription subscription = memberSubscriptionRepository.findById(subscriptionId)
				.orElseThrow(() -> new NoActiveSubscriptionException("Subscription not found: " + subscriptionId));
		subscription.setStatus(SubscriptionStatus.CANCELLED.getCode());
		subscription.setSupervisor(this.operatorRepository.findById(supervisorId).orElse(null));
		memberSubscriptionRepository.save(subscription);
		memberRepository.clearCurrentSubscription(subscriptionId);
		return new MemberSubscriptionDTO(subscription);
	}

	@Override
	public MemberSubscriptionDTO getActiveSubscription(Long memberId) {
		return memberSubscriptionRepository.findByMemberIdAndStatus(memberId, SubscriptionStatus.ACTIVE.getCode())
				.map(MemberSubscriptionDTO::new)
				.orElseThrow(() -> new NoActiveSubscriptionException("No active subscription for member: " + memberId));
	}

	@Override
	@Transactional
	public ChargeResultDTO chargeSessionFee(ChargeMemberSessionRequest request) {
		Member member = memberRepository.findById(request.getMemberId())
				.orElseThrow(() -> new IllegalArgumentException("Member not found: " + request.getMemberId()));

		MemberSubscription subscription = member.getCurrentSubscription();
		if (subscription == null) {
			return ChargeResultDTO.failure("NO_ACTIVE_SUBSCRIPTION");
		}

		int updated = memberSubscriptionRepository.deductBalance(subscription.getId(), request.getAmount());
		if (updated == 0) {
			return ChargeResultDTO.failure("INSUFFICIENT_BALANCE_OR_EXPIRED");
		}

		MemberSubscription refreshed = memberSubscriptionRepository.findById(subscription.getId())
				.orElseThrow(() -> new NoActiveSubscriptionException("Subscription not found: " + subscription.getId()));

		ParkingSession session = CommonValidators.validLong(request.getSessionId())
				? parkingSessionRepository.findById(request.getSessionId()).orElse(null)
				: null;

		recordTransaction(refreshed, member, session, BalanceTransactionType.DEBIT, request.getAmount(), refreshed.getBalance(),
				"Session charge: " + request.getSessionId(), null);

		return ChargeResultDTO.success(request.getAmount(), refreshed.getBalance(), null, refreshed.getId());
	}

	@Override
	@Scheduled(cron = "0 0 * * * *")
	@Transactional
	public void expireOverdueSubscriptions() {
		memberSubscriptionRepository.expireOverdueSubscriptions();
		memberRepository.clearExpiredCurrentSubscriptions();
	}

	private void recordTransaction(MemberSubscription subscription, Member member, ParkingSession session,
			BalanceTransactionType type, BigDecimal amount, BigDecimal balanceAfter, String remark, Long superisorId) {
		MemberBalanceTransaction tx = new MemberBalanceTransaction();
		tx.setSubscription(subscription);
		tx.setMember(member);
		tx.setSession(session);
		tx.setTransactionType(type.getCode());
		tx.setAmount(amount);
		tx.setBalanceAfter(balanceAfter);
		tx.setRemark(remark);
		if(CommonValidators.validLong(superisorId)) {
			Operator operator = this.operatorRepository.findById(superisorId)
					.orElse(null);
			tx.setSupervisor(operator);
		}
		
		memberBalanceTransactionRepository.save(tx);
	}

	private Date addDays(Date base, Integer days) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(base);
		calendar.add(Calendar.DAY_OF_MONTH, days != null ? days : 0);
		return calendar.getTime();
	}
}