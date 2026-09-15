package org.flexitech.projects.icpms.persistence.entities.member;

import java.math.BigDecimal;
import java.util.Map;

import org.flexitech.projects.icpms.common.TableNames;
import org.flexitech.projects.icpms.persistence.BasedEntity;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = TableNames.MEMBER_PLAN_TBL)
@Getter
@Setter
public class MemberPlan extends BasedEntity {

	@Column(unique = true, nullable = false)
	private String code;

	private String name;

	private String description;

	private BigDecimal price;

	@Column(name = "duration_days")
	private Integer durationDays;

	@Column(name = "granted_balance")
	private BigDecimal grantedBalance;

	@Column(name = "discount_percent")
	private BigDecimal discountPercent = BigDecimal.ZERO;

	@Column(name = "free_minutes")
	private Integer freeMinutes = 0;

	@Column(name = "max_vehicles")
	private Integer maxVehicles = 1;

	@Column(name = "is_active")
	private Boolean isActive = true;

	private Integer status = 1;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "extra_features")
	private Map<String, Object> extraFeatures;
}