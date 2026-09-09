package org.flexitech.projects.icpms.persistence.entities.setting;

import org.flexitech.projects.icpms.common.TableNames;
import org.flexitech.projects.icpms.persistence.BasedEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = TableNames.SYSTEM_SETTING_TBL)
@Getter
@Setter
@NoArgsConstructor
public class SystemSetting extends BasedEntity{
	
	@Column(unique = true, nullable = false)
	private String code;
	private String description;
	private String value;
	private Integer editableStatus;
	private Integer sequence;
	@Column(name = "input_type")
	private Integer inputType;
	
	private String icon;
	
	@Column(name = "sync_to_operator")
	private Integer syncToOperatorStatus;
	
}