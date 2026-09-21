package org.flexitech.projects.icpms.persistence.repositories.gate;

import org.flexitech.projects.icpms.persistence.entities.gate.GateDeviceDiagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GateDeviceDiagnosisRepository extends JpaRepository<GateDeviceDiagnosis, Long>, JpaSpecificationExecutor<GateDeviceDiagnosis>{

}
