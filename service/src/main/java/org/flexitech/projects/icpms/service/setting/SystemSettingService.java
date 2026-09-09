package org.flexitech.projects.icpms.service.setting;

import java.util.List;

import org.flexitech.projects.icpms.common.exceptions.SystemSettingNotFoundException;
import org.flexitech.projects.icpms.dto.SearchResultDTO;
import org.flexitech.projects.icpms.dto.system_setting.SystemSettingDTO;
import org.flexitech.projects.icpms.dto.system_setting.SystemSettingSearchDTO;

public interface SystemSettingService {

	SystemSettingDTO manageSystemSetting(SystemSettingDTO dto) throws SystemSettingNotFoundException;
	SearchResultDTO<SystemSettingDTO> searchSystemSetting(SystemSettingSearchDTO searchDTO);
	
	List<SystemSettingDTO> batchUpdateSystemSettings(List<SystemSettingDTO> settings) throws SystemSettingNotFoundException;
	
	SystemSettingDTO getByCode(String code)throws SystemSettingNotFoundException;
	
	List<SystemSettingDTO> getAllOperatorSettings();
	
}
