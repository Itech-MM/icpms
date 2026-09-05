package org.flexitech.projects.icpms.persistence.repositories.audit_logs;

import java.util.List;

import org.flexitech.projects.icpms.persistence.entities.audit_logs.VehicleAlertLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface VehicleAlertLogRepository extends JpaRepository<VehicleAlertLog, Long>, JpaSpecificationExecutor<VehicleAlertLog> {

	List<VehicleAlertLog> findByPlateNumberOrderByCreatedTimeDesc(String plateNumber);

	List<VehicleAlertLog> findByAlertTypeOrderByCreatedTimeDesc(Integer alertType);
}