package org.flexitech.projects.icpms.service.tariff;

import java.util.List;

import org.flexitech.projects.icpms.dto.SearchResultDTO;
import org.flexitech.projects.icpms.dto.tariff.TariffDTO;
import org.flexitech.projects.icpms.dto.tariff.TariffRateDTO;
import org.flexitech.projects.icpms.dto.tariff.TariffSearchDTO;
import org.springframework.data.domain.Pageable;

public interface TariffService {
	TariffDTO manageTariff(TariffDTO dto) throws Exception;
	TariffDTO getTariffById(Long id) throws Exception;
	SearchResultDTO<TariffDTO> searchTariffs(TariffSearchDTO searchDTO, Pageable pageable) throws Exception;
	List<TariffDTO> findAllActiveTariffs();
	boolean deleteTariff(Long id) throws Exception;

	TariffRateDTO addRate(TariffRateDTO dto) throws Exception;
	boolean deleteRate(Long rateId) throws Exception;
	List<TariffRateDTO> getRates(Long tariffId);
}
