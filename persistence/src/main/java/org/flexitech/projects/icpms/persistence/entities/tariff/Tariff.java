package org.flexitech.projects.icpms.persistence.entities.tariff;

import org.flexitech.projects.icpms.common.TableNames;
import org.flexitech.projects.icpms.persistence.BasedEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = TableNames.TARIFF_TBL)
@Getter
@Setter
public class Tariff extends BasedEntity {

	private String name;
	private String code;
	private String description;

	@Column(name = "is_active")
	private Boolean isActive = true;

	private Integer status = 1;
}
