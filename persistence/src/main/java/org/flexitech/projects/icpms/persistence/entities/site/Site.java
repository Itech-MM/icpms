package org.flexitech.projects.icpms.persistence.entities.site;

import org.flexitech.projects.icpms.common.TableNames;
import org.flexitech.projects.icpms.persistence.BasedEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = TableNames.SITE_TBL)
@Getter
@Setter
public class Site extends BasedEntity {

	private String name;
	private String code;
	private String address;

	@Column(name = "total_capacity")
	private Integer totalCapacity;

	private Integer status = 1;
}
