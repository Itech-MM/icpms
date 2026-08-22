package org.flexitech.projects.icpms.admin.controller;

import java.math.BigDecimal;

import org.flexitech.projects.icpms.common.enums.ParkingSessionStatus;
import org.flexitech.projects.icpms.common.enums.PaymentMethod;
import org.flexitech.projects.icpms.common.enums.PaymentStatus;
import org.flexitech.projects.icpms.dto.SearchResultDTO;
import org.flexitech.projects.icpms.dto.payment.PaymentDTO;
import org.flexitech.projects.icpms.dto.payment.PaymentSearchDTO;
import org.flexitech.projects.icpms.dto.session.ParkingSessionDTO;
import org.flexitech.projects.icpms.dto.session.ParkingSessionSearchDTO;
import org.flexitech.projects.icpms.service.payment.PaymentService;
import org.flexitech.projects.icpms.service.session.ParkingSessionService;
import org.flexitech.projects.icpms.service.site.SiteService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ReportController {

	private final ParkingSessionService sessionService;
	private final PaymentService paymentService;
	private final SiteService siteService;

	public ReportController(ParkingSessionService sessionService, PaymentService paymentService, SiteService siteService) {
		this.sessionService = sessionService;
		this.paymentService = paymentService;
		this.siteService = siteService;
	}

	@GetMapping("/reports/parking-sessions")
	public String sessionsReport(ParkingSessionSearchDTO searchDTO,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size,
			Model model) throws Exception {

		Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
		SearchResultDTO<ParkingSessionDTO> result = sessionService.searchSessions(searchDTO, pageable);

		StringBuilder qs = new StringBuilder("/reports/parking-sessions?");
		if (searchDTO.getPlateNumber() != null) qs.append("plateNumber=").append(searchDTO.getPlateNumber()).append("&");
		if (searchDTO.getSiteId() != null) qs.append("siteId=").append(searchDTO.getSiteId()).append("&");
		if (searchDTO.getStatus() != null) qs.append("status=").append(searchDTO.getStatus()).append("&");
		if (searchDTO.getFromDate() != null) qs.append("fromDate=").append(searchDTO.getFromDate()).append("&");
		if (searchDTO.getToDate() != null) qs.append("toDate=").append(searchDTO.getToDate()).append("&");
		qs.append("size=").append(size).append("&page=");

		model.addAttribute("result", result);
		model.addAttribute("searchDTO", searchDTO);
		model.addAttribute("statuses", ParkingSessionStatus.getAll());
		model.addAttribute("sites", siteService.findAllActiveSites());
		model.addAttribute("pageUrlPrefix", qs.toString());
		model.addAttribute("pageTitle", "Parking Session Report");
		model.addAttribute("activeMenu", "report-sessions");
		return "reports/sessions";
	}

	@GetMapping("/reports/payments")
	public String paymentsReport(PaymentSearchDTO searchDTO,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size,
			Model model) throws Exception {

		Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
		SearchResultDTO<PaymentDTO> result = paymentService.searchPayments(searchDTO, pageable);
		BigDecimal total = paymentService.sumAmount(searchDTO);

		StringBuilder qs = new StringBuilder("/reports/payments?");
		if (searchDTO.getPlateNumber() != null) qs.append("plateNumber=").append(searchDTO.getPlateNumber()).append("&");
		if (searchDTO.getMethod() != null) qs.append("method=").append(searchDTO.getMethod()).append("&");
		if (searchDTO.getStatus() != null) qs.append("status=").append(searchDTO.getStatus()).append("&");
		if (searchDTO.getFromDate() != null) qs.append("fromDate=").append(searchDTO.getFromDate()).append("&");
		if (searchDTO.getToDate() != null) qs.append("toDate=").append(searchDTO.getToDate()).append("&");
		qs.append("size=").append(size).append("&page=");

		model.addAttribute("result", result);
		model.addAttribute("searchDTO", searchDTO);
		model.addAttribute("totalAmount", total);
		model.addAttribute("methods", PaymentMethod.getAll());
		model.addAttribute("statuses", PaymentStatus.getAll());
		model.addAttribute("pageUrlPrefix", qs.toString());
		model.addAttribute("pageTitle", "Payment Report");
		model.addAttribute("activeMenu", "report-payments");
		return "reports/payments";
	}
}
