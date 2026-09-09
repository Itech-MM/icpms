package org.flexitech.projects.icpms.persistence.repositories.setting;

import java.util.List;
import java.util.Optional;

import org.flexitech.projects.icpms.persistence.entities.setting.SystemSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SystemSettingRepository extends JpaRepository<SystemSetting, Long>, JpaSpecificationExecutor<SystemSetting> {
	Optional<SystemSetting> findByCode(String code);
	
	List<SystemSetting> findBySyncToOperatorStatus(Integer syncToOperatorStatus);

}
