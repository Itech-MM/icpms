package org.flexitech.projects.icpms.persistence.entities.member;

import java.math.BigDecimal;
import java.util.Date;

import org.flexitech.projects.icpms.common.TableNames;
import org.flexitech.projects.icpms.persistence.BasedEntity;
import org.flexitech.projects.icpms.persistence.entities.operator.Operator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = TableNames.MEMBER_SUBSCRIPTION_TBL)
@Getter
@Setter
public class MemberSubscription extends BasedEntity {

	@ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
	@JoinColumn(name = "plan_id")
	private MemberPlan plan;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "start_date")
	private Date startDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "end_date")
	private Date endDate;

	@Column(name = "initial_balance")
	private BigDecimal initialBalance;

	private BigDecimal balance;

	private Integer status = 1;

	@Version
	private Long version;
	

	@ManyToOne
	@JoinColumn(name = "supervisor_id")
	private Operator supervisor;

}