package org.flexitech.projects.icpms.dto.member;

import java.math.BigDecimal;
import java.util.Date;

import org.flexitech.projects.icpms.common.CommonConstants;
import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.common.enums.SubscriptionStatus;
import org.flexitech.projects.icpms.common.utils.DateUtils;
import org.flexitech.projects.icpms.dto.CommonDTO;
import org.flexitech.projects.icpms.persistence.entities.member.MemberSubscription;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberSubscriptionDTO extends CommonDTO {

	private Long memberId;
	private String memberName;
	private Long planId;
	private String planCode;
	private String planName;
	private String startDate;
	private String endDate;
	private BigDecimal initialBalance;
	private BigDecimal balance;
	private BigDecimal discountPercent;
	private Integer freeMinutes;
	private Integer status = 1;
	private String statusDesc;
	private Boolean isExpired = false;

	public MemberSubscriptionDTO(MemberSubscription subscription) {
		super(subscription);
		if (CommonValidators.isValidObject(subscription.getMember())) {
			this.memberId = subscription.getMember().getId();
			this.memberName = subscription.getMember().getName();
		}
		if (CommonValidators.isValidObject(subscription.getPlan())) {
			this.planId = subscription.getPlan().getId();
			this.planCode = subscription.getPlan().getCode();
			this.planName = subscription.getPlan().getName();
			this.discountPercent = subscription.getPlan().getDiscountPercent();
			this.freeMinutes = subscription.getPlan().getFreeMinutes();
		}
		if (CommonValidators.isValidObject(subscription.getStartDate())) {
			this.startDate = DateUtils.dateToString(subscription.getStartDate(), CommonConstants.STANDARD_DB_DATE_FORMAT);
		}
		if (CommonValidators.isValidObject(subscription.getEndDate())) {
			this.endDate = DateUtils.dateToString(subscription.getEndDate(), CommonConstants.STANDARD_DB_DATE_FORMAT);
			Date now = new Date();
			this.isExpired = subscription.getEndDate().before(now);
		}
		this.initialBalance = subscription.getInitialBalance();
		this.balance = subscription.getBalance();
		this.status = subscription.getStatus();
		this.statusDesc = SubscriptionStatus.getDescByCode(status);
	}
}