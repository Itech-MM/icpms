package org.flexitech.projects.icpms.service.payment;

import java.math.BigDecimal;
import java.util.stream.Collectors;

import org.flexitech.projects.icpms.dto.SearchResultDTO;
import org.flexitech.projects.icpms.dto.payment.PaymentDTO;
import org.flexitech.projects.icpms.dto.payment.PaymentSearchDTO;
import org.flexitech.projects.icpms.persistence.entities.payment.Payment;
import org.flexitech.projects.icpms.persistence.repositories.payment.PaymentRepository;
import org.flexitech.projects.icpms.service.specifications.payment.PaymentSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

@Service
public class PaymentServiceImpl implements PaymentService {

	private final PaymentRepository paymentRepository;

	public PaymentServiceImpl(PaymentRepository paymentRepository) {
		this.paymentRepository = paymentRepository;
	}

	@Override
	public SearchResultDTO<PaymentDTO> searchPayments(PaymentSearchDTO searchDTO, Pageable pageable) throws Exception {
		Specification<Payment> spec = PaymentSpecification.withSearchCriteria(searchDTO);
		Page<Payment> page = paymentRepository.findAll(spec, pageable);

		SearchResultDTO<PaymentDTO> result = new SearchResultDTO<>();
		result.setPageNo(page.getNumber());
		result.setLimit(page.getSize());
		result.setTotalPage(page.getTotalPages());
		result.setTotalRecords((int) page.getTotalElements());
		result.setPageCount(page.getNumberOfElements());
		result.setHasNextPage(page.hasNext());
		result.setResults(page.getContent().stream().map(PaymentDTO::new).collect(Collectors.toList()));
		return result;
	}

	@Override
	public PaymentDTO getPaymentById(Long id) throws Exception {
		Payment payment = this.paymentRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Payment doesn't exist!"));
		return new PaymentDTO(payment);
	}

	@Override
	public BigDecimal sumAmount(PaymentSearchDTO searchDTO) {
		Specification<Payment> spec = PaymentSpecification.withSearchCriteria(searchDTO);
		return this.paymentRepository.findAll(spec).stream()
				.map(Payment::getAmount)
				.filter(a -> a != null)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}
}
