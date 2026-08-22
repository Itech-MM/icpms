package org.flexitech.projects.icpms.persistence.entities.role;

import org.flexitech.projects.icpms.common.TableNames;
import org.flexitech.projects.icpms.persistence.BasedEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = TableNames.ROLE_TBL)
public class Role extends BasedEntity{

	private String name;
	private String code;
	private Integer status = 1;
	
}
