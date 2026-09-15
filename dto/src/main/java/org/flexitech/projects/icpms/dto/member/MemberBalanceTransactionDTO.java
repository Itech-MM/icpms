package org.flexitech.projects.icpms.dto.member;

import java.math.BigDecimal;

import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.common.enums.BalanceTransactionType;
import org.flexitech.projects.icpms.dto.CommonDTO;
import org.flexitech.projects.icpms.persistence.entities.member.MemberBalanceTransaction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberBalanceTransactionDTO extends CommonDTO {

	private Long subscriptionId;
	private Long memberId;
	private Long sessionId;
	private Integer transactionType;
	private String transactionTypeDesc;
	private BigDecimal amount;
	private BigDecimal balanceAfter;
	private String remark;

	public MemberBalanceTransactionDTO(MemberBalanceTransaction transaction) {
		super(transaction);
		if (CommonValidators.isValidObject(transaction.getSubscription())) {
			this.subscriptionId = transaction.getSubscription().getId();
		}
		if (CommonValidators.isValidObject(transaction.getMember())) {
			this.memberId = transaction.getMember().getId();
		}
		if (CommonValidators.isValidObject(transaction.getSession())) {
			this.sessionId = transaction.getSession().getId();
		}
		this.transactionType = transaction.getTransactionType();
		this.transactionTypeDesc = BalanceTransactionType.getDescByCode(transactionType);
		this.amount = transaction.getAmount();
		this.balanceAfter = transaction.getBalanceAfter();
		this.remark = transaction.getRemark();
	}
}