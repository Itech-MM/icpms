package org.flexitech.projects.icpms.service.payment;

import java.math.BigDecimal;

import org.flexitech.projects.icpms.dto.SearchResultDTO;
import org.flexitech.projects.icpms.dto.payment.PaymentDTO;
import org.flexitech.projects.icpms.dto.payment.PaymentSearchDTO;
import org.springframework.data.domain.Pageable;

public interface PaymentService {
	SearchResultDTO<PaymentDTO> searchPayments(PaymentSearchDTO searchDTO, Pageable pageable) throws Exception;
	PaymentDTO getPaymentById(Long id) throws Exception;
	BigDecimal sumAmount(PaymentSearchDTO searchDTO);
}
