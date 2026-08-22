package org.flexitech.projects.icpms.service.tariff;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.common.enums.ActiveStatus;
import org.flexitech.projects.icpms.dto.SearchResultDTO;
import org.flexitech.projects.icpms.dto.tariff.TariffDTO;
import org.flexitech.projects.icpms.dto.tariff.TariffRateDTO;
import org.flexitech.projects.icpms.dto.tariff.TariffSearchDTO;
import org.flexitech.projects.icpms.persistence.entities.tariff.Tariff;
import org.flexitech.projects.icpms.persistence.entities.tariff.TariffRate;
import org.flexitech.projects.icpms.persistence.entities.user.User;
import org.flexitech.projects.icpms.persistence.repositories.tariff.TariffRateRepository;
import org.flexitech.projects.icpms.persistence.repositories.tariff.TariffRepository;
import org.flexitech.projects.icpms.service.auth.AuthenticationService;
import org.flexitech.projects.icpms.service.specifications.tariff.TariffSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

@Service
public class TariffServiceImpl implements TariffService {

	private final TariffRepository tariffRepository;
	private final TariffRateRepository tariffRateRepository;
	private final AuthenticationService authenticationService;

	public TariffServiceImpl(TariffRepository tariffRepository, TariffRateRepository tariffRateRepository,
			AuthenticationService authenticationService) {
		this.tariffRepository = tariffRepository;
		this.tariffRateRepository = tariffRateRepository;
		this.authenticationService = authenticationService;
	}

	@Override
	public TariffDTO manageTariff(TariffDTO dto) throws Exception {
		Tariff tariff;
		User user = this.authenticationService.getLoggedInUser();
		if (CommonValidators.validLong(dto.getId())) {
			tariff = this.tariffRepository.findById(dto.getId())
					.orElseThrow(() -> new EntityNotFoundException("Tariff doesn't exist!"));
			tariff.setUpdatedTime(new Date());
			tariff.setUpdatedBy(user);
		} else {
			tariff = new Tariff();
			tariff.setCreatedTime(new Date());
			tariff.setCreatedBy(user);
		}
		tariff.setName(dto.getName());
		tariff.setCode(dto.getCode());
		tariff.setDescription(dto.getDescription());
		tariff.setIsActive(dto.getIsActive() == null || dto.getIsActive());
		tariff.setStatus(CommonValidators.isValidObject(dto.getStatus()) ? dto.getStatus() : ActiveStatus.ACTIVE.getCode());

		Tariff saved = this.tariffRepository.save(tariff);
		TariffDTO result = new TariffDTO(saved);
		result.setRates(getRates(saved.getId()));
		return result;
	}

	@Override
	public TariffDTO getTariffById(Long id) throws Exception {
		Tariff tariff = this.tariffRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Tariff doesn't exist!"));
		TariffDTO dto = new TariffDTO(tariff);
		dto.setRates(getRates(id));
		return dto;
	}

	@Override
	public SearchResultDTO<TariffDTO> searchTariffs(TariffSearchDTO searchDTO, Pageable pageable) throws Exception {
		Specification<Tariff> spec = TariffSpecification.withSearchCriteria(searchDTO);
		Page<Tariff> page = tariffRepository.findAll(spec, pageable);

		SearchResultDTO<TariffDTO> result = new SearchResultDTO<>();
		result.setPageNo(page.getNumber());
		result.setLimit(page.getSize());
		result.setTotalPage(page.getTotalPages());
		result.setTotalRecords((int) page.getTotalElements());
		result.setPageCount(page.getNumberOfElements());
		result.setHasNextPage(page.hasNext());
		result.setResults(page.getContent().stream().map(t -> {
			TariffDTO d = new TariffDTO(t);
			d.setRates(getRates(t.getId()));
			return d;
		}).collect(Collectors.toList()));
		return result;
	}

	@Override
	public List<TariffDTO> findAllActiveTariffs() {
		return this.tariffRepository.findByIsActiveTrue().stream().map(TariffDTO::new).collect(Collectors.toList());
	}

	@Override
	public boolean deleteTariff(Long id) throws Exception {
		Tariff tariff = this.tariffRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Tariff doesn't exist!"));
		this.tariffRateRepository.deleteByTariffId(id);
		this.tariffRepository.delete(tariff);
		return true;
	}

	@Override
	public TariffRateDTO addRate(TariffRateDTO dto) throws Exception {
		TariffRate rate;
		System.out.println("Tarif rate id:: " + dto.getId());
		/*
		 * if (CommonValidators.validLong(dto.getId())) { rate =
		 * this.tariffRateRepository.findById(dto.getId()) .orElseThrow(() -> new
		 * EntityNotFoundException("Tariff rate doesn't exist!")); } else
		 */{
			rate = new TariffRate();
			rate.setCreatedTime(new Date());
			Tariff tariff = this.tariffRepository.findById(dto.getTariffId())
					.orElseThrow(() -> new EntityNotFoundException("Tariff doesn't exist!"));
			rate.setTariff(tariff);
		}
		rate.setFromMinute(dto.getFromMinute());
		rate.setToMinute(dto.getToMinute());
		rate.setAmount(dto.getAmount());

		TariffRate saved = this.tariffRateRepository.save(rate);
		return new TariffRateDTO(saved);
	}

	@Override
	public boolean deleteRate(Long rateId) throws Exception {
		TariffRate rate = this.tariffRateRepository.findById(rateId)
				.orElseThrow(() -> new EntityNotFoundException("Tariff rate doesn't exist!"));
		this.tariffRateRepository.delete(rate);
		return true;
	}

	@Override
	public List<TariffRateDTO> getRates(Long tariffId) {
		return this.tariffRateRepository.findByTariffIdOrderByFromMinuteAsc(tariffId)
				.stream().map(TariffRateDTO::new).collect(Collectors.toList());
	}
}
