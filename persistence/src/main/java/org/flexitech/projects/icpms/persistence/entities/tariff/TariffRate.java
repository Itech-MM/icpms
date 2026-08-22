package org.flexitech.projects.icpms.persistence.entities.tariff;

import java.math.BigDecimal;

import org.flexitech.projects.icpms.common.TableNames;
import org.flexitech.projects.icpms.persistence.BasedEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = TableNames.TARIFF_RATE_TBL)
@Getter
@Setter
public class TariffRate extends BasedEntity {

	@ManyToOne
	@JoinColumn(name = "tariff_id")
	private Tariff tariff;

	@Column(name = "from_minute")
	private Integer fromMinute;

	@Column(name = "to_minute")
	private Integer toMinute;

	private BigDecimal amount;

	private Integer status = 1;
}
