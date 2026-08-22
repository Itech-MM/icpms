package org.flexitech.projects.icpms.persistence.repositories.tariff;

import java.util.List;

import org.flexitech.projects.icpms.persistence.entities.tariff.TariffRate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TariffRateRepository extends JpaRepository<TariffRate, Long> {
	List<TariffRate> findByTariffIdOrderByFromMinuteAsc(Long tariffId);
	void deleteByTariffId(Long tariffId);
}
