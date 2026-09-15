package org.flexitech.projects.icpms.persistence.entities.member;

import java.math.BigDecimal;

import org.flexitech.projects.icpms.common.TableNames;
import org.flexitech.projects.icpms.persistence.BasedEntity;
import org.flexitech.projects.icpms.persistence.entities.operator.Operator;
import org.flexitech.projects.icpms.persistence.entities.session.ParkingSession;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = TableNames.MEMBER_BALANCE_TRANSACTION_TBL)
@Getter
@Setter
public class MemberBalanceTransaction extends BasedEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subscription_id")
	private MemberSubscription subscription;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "session_id")
	private ParkingSession session;

	@Column(name = "transaction_type")
	private Integer transactionType;

	private BigDecimal amount;

	@Column(name = "balance_after")
	private BigDecimal balanceAfter;
	
	@ManyToOne
	@JoinColumn(name = "supervisor_id")
	private Operator supervisor;

	private String remark;
}