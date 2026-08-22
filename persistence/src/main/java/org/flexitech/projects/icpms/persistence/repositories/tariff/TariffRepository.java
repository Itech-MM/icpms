package org.flexitech.projects.icpms.persistence.repositories.tariff;

import java.util.List;

import org.flexitech.projects.icpms.persistence.entities.tariff.Tariff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TariffRepository extends JpaRepository<Tariff, Long>, JpaSpecificationExecutor<Tariff> {
	List<Tariff> findByIsActiveTrue();
}
