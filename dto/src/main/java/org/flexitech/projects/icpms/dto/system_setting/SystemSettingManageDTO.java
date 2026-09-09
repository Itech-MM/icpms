package org.flexitech.projects.icpms.dto.system_setting;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SystemSettingManageDTO {
	private List<SystemSettingDTO> settings;
}