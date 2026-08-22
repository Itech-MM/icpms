package org.flexitech.projects.icpms.dto.tariff;

import java.util.List;

import org.flexitech.projects.icpms.dto.CommonDTO;
import org.flexitech.projects.icpms.persistence.entities.tariff.Tariff;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TariffDTO extends CommonDTO {

	@NotBlank
	private String name;
	private String code;
	private String description;
	private Boolean isActive = true;
	private Integer status = 1;
	private String statusDesc;
	private List<TariffRateDTO> rates;

	public TariffDTO(Tariff tariff) {
		super(tariff);
		this.name = tariff.getName();
		this.code = tariff.getCode();
		this.description = tariff.getDescription();
		this.isActive = tariff.getIsActive();
		this.status = tariff.getStatus();
	}
}
