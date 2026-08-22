package org.flexitech.projects.icpms.dto.site;

import org.flexitech.projects.icpms.common.enums.ActiveStatus;
import org.flexitech.projects.icpms.dto.CommonDTO;
import org.flexitech.projects.icpms.persistence.entities.site.Site;

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
public class SiteDTO extends CommonDTO {

	@NotBlank
	private String name;
	@NotBlank
	private String code;
	private String address;
	@NotNull
	private Integer totalCapacity;
	private Integer status = 1;
	private String statusDesc;

	public SiteDTO(Site site) {
		super(site);
		this.name = site.getName();
		this.code = site.getCode();
		this.address = site.getAddress();
		this.totalCapacity = site.getTotalCapacity();
		this.status = site.getStatus();
		this.statusDesc = ActiveStatus.getDescByCode(status);
	}
}
