package org.flexitech.projects.icpms.dto.system_setting;

import org.flexitech.projects.icpms.dto.CommonDTO;
import org.flexitech.projects.icpms.persistence.entities.setting.SystemSetting;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SystemSettingDTO extends CommonDTO{
	private String code;
	private String description;
	private String value;
	private Integer editableStatus;
	private Integer sequence;
	private Integer inputType;
	private String icon;
	
	public SystemSettingDTO(SystemSetting s) {
		super(s);
		this.code = s.getCode();
		this.description = s.getDescription();
		this.value = s.getValue();
		this.editableStatus = s.getEditableStatus();
		this.sequence = s.getSequence();
		this.inputType = s.getInputType();
		this.icon = s.getIcon();
	}
}