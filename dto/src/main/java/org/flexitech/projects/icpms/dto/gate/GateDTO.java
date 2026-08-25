package org.flexitech.projects.icpms.dto.gate;

import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.common.enums.ActiveStatus;
import org.flexitech.projects.icpms.common.enums.GateType;
import org.flexitech.projects.icpms.dto.CommonDTO;
import org.flexitech.projects.icpms.dto.tariff.TariffDTO;
import org.flexitech.projects.icpms.persistence.entities.gate.Gate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GateDTO extends CommonDTO {

	@NotNull
	private Long siteId;
	private String siteName;
	@NotBlank
	private String name;
	private String code;
	@NotNull
	private Integer type;
	private String typeDesc;
	private Integer status = 1;
	private String statusDesc;
	private String gateIpAddress;
	
	private TariffDTO tariff;
	private Long tariffId;
	private String tariffName;

	public GateDTO(Gate gate) {
		super(gate);
		if (CommonValidators.isValidObject(gate.getSite())) {
			this.siteId = gate.getSite().getId();
			this.siteName = gate.getSite().getName();
		}
		this.name = gate.getName();
		this.code = gate.getCode();
		this.type = gate.getType();
		this.typeDesc = GateType.getDescByCode(type);
		this.status = gate.getStatus();
		this.statusDesc = ActiveStatus.getDescByCode(status);
		this.gateIpAddress = gate.getGateIpAddress();
		
		if(gate.getTariff() != null) {
			this.tariff = new TariffDTO(gate.getTariff());
			this.tariffId = gate.getTariff().getId();
			this.tariffName = gate.getTariff().getName();
		}
		
	}
}
