package org.flexitech.projects.icpms.dto.tariff;

import java.math.BigDecimal;

import org.flexitech.projects.icpms.dto.CommonDTO;
import org.flexitech.projects.icpms.persistence.entities.tariff.TariffRate;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TariffRateDTO extends CommonDTO {

	private Long tariffId;
	@NotNull
	private Integer fromMinute;
	private Integer toMinute;
	@NotNull
	private BigDecimal amount;

	public TariffRateDTO(TariffRate rate) {
		super(rate);
		if (rate.getTariff() != null) {
			this.tariffId = rate.getTariff().getId();
		}
		this.fromMinute = rate.getFromMinute();
		this.toMinute = rate.getToMinute();
		this.amount = rate.getAmount();
	}
}
