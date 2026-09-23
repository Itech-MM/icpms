package org.flexitech.projects.icpms.admin.controller;

import java.math.BigDecimal;

import org.flexitech.projects.icpms.common.MenuCodeConstants;
import org.flexitech.projects.icpms.common.enums.ParkingSessionStatus;
import org.flexitech.projects.icpms.common.enums.PaymentMethod;
import org.flexitech.projects.icpms.common.enums.PaymentStatus;
import org.flexitech.projects.icpms.common.enums.ShiftStatus;
import org.flexitech.projects.icpms.dto.SearchResultDTO;
import org.flexitech.projects.icpms.dto.operator.OperatorShiftDTO;
import org.flexitech.projects.icpms.dto.operator.OperatorShiftSearchDTO;
import org.flexitech.projects.icpms.dto.payment.PaymentDTO;
import org.flexitech.projects.icpms.dto.payment.PaymentSearchDTO;
import org.flexitech.projects.icpms.dto.session.ParkingSessionDTO;
import org.flexitech.projects.icpms.dto.session.ParkingSessionSearchDTO;
import org.flexitech.projects.icpms.service.operator.OperatorShiftService;
import org.flexitech.projects.icpms.service.payment.PaymentService;
import org.flexitech.projects.icpms.service.session.ParkingSessionService;
import org.flexitech.projects.icpms.service.site.SiteService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ReportController {

	private final ParkingSessionService sessionService;
	private final PaymentService paymentService;
	private final SiteService siteService;
	private final OperatorShiftService operatorShiftService;

	@GetMapping("/reports/parking-sessions")
	@PreAuthorize("@menuSecurity.hasMenuAccess('" + MenuCodeConstants.MENU_REPORT_SESSIONS
			+ "') or @menuSecurity.hasMenuView('" + MenuCodeConstants.MENU_REPORT_SESSIONS + "')")
	public String sessionsReport(ParkingSessionSearchDTO searchDTO, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, Model model) throws Exception {

		Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
		SearchResultDTO<ParkingSessionDTO> result = sessionService.searchSessions(searchDTO, pageable);

		StringBuilder qs = new StringBuilder("/reports/parking-sessions?");
		if (searchDTO.getPlateNumber() != null)
			qs.append("plateNumber=").append(searchDTO.getPlateNumber()).append("&");
		if (searchDTO.getSiteId() != null)
			qs.append("siteId=").append(searchDTO.getSiteId()).append("&");
		if (searchDTO.getStatus() != null)
			qs.append("status=").append(searchDTO.getStatus()).append("&");
		if (searchDTO.getFromDate() != null)
			qs.append("fromDate=").append(searchDTO.getFromDate()).append("&");
		if (searchDTO.getToDate() != null)
			qs.append("toDate=").append(searchDTO.getToDate()).append("&");
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
	@PreAuthorize("@menuSecurity.hasMenuAccess('" + MenuCodeConstants.MENU_REPORT_PAYMENTS
			+ "') or @menuSecurity.hasMenuView('" + MenuCodeConstants.MENU_REPORT_PAYMENTS + "')")
	public String paymentsReport(PaymentSearchDTO searchDTO, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, Model model) throws Exception {

		Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
		SearchResultDTO<PaymentDTO> result = paymentService.searchPayments(searchDTO, pageable);
		BigDecimal total = paymentService.sumAmount(searchDTO);

		StringBuilder qs = new StringBuilder("/reports/payments?");
		if (searchDTO.getPlateNumber() != null)
			qs.append("plateNumber=").append(searchDTO.getPlateNumber()).append("&");
		if (searchDTO.getMethod() != null)
			qs.append("method=").append(searchDTO.getMethod()).append("&");
		if (searchDTO.getStatus() != null)
			qs.append("status=").append(searchDTO.getStatus()).append("&");
		if (searchDTO.getFromDate() != null)
			qs.append("fromDate=").append(searchDTO.getFromDate()).append("&");
		if (searchDTO.getToDate() != null)
			qs.append("toDate=").append(searchDTO.getToDate()).append("&");
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

	@GetMapping("/reports/shifts")
	@PreAuthorize("@menuSecurity.hasMenuAccess('" + MenuCodeConstants.MENU_REPORT_SHIFT
			+ "') or @menuSecurity.hasMenuView('" + MenuCodeConstants.MENU_REPORT_SHIFT + "')")
	public String shiftsReport(OperatorShiftSearchDTO searchDTO, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, Model model) throws Exception {

		searchDTO.setPageNo(page + 1);
		searchDTO.setLimit(size);
		SearchResultDTO<OperatorShiftDTO> result = operatorShiftService.searchOperatorShift(searchDTO, false);

		StringBuilder qs = new StringBuilder("/reports/shifts?");
		if (searchDTO.getShiftCode() != null)
			qs.append("shiftCode=").append(searchDTO.getShiftCode()).append("&");
		if (searchDTO.getShiftStatus() != null)
			qs.append("shiftStatus=").append(searchDTO.getShiftStatus()).append("&");
		if (searchDTO.getOperatorId() != null)
			qs.append("operatorId=").append(searchDTO.getOperatorId()).append("&");
		if (searchDTO.getGateId() != null)
			qs.append("gateId=").append(searchDTO.getGateId()).append("&");
		if (searchDTO.getFromDateTime() != null)
			qs.append("fromDateTime=").append(searchDTO.getFromDateTime()).append("&");
		if (searchDTO.getToDateTime() != null)
			qs.append("toDateTime=").append(searchDTO.getToDateTime()).append("&");
		qs.append("size=").append(size).append("&page=");

		model.addAttribute("result", result);
		model.addAttribute("searchDTO", searchDTO);
		model.addAttribute("statuses", ShiftStatus.getAll());
		model.addAttribute("pageUrlPrefix", qs.toString());
		model.addAttribute("pageTitle", "Shift Report");
		model.addAttribute("activeMenu", "report-shifts");
		return "reports/shifts";
	}
	
	@GetMapping("/reports/shifts/{id}")
	@PreAuthorize("@menuSecurity.hasMenuAccess('" + MenuCodeConstants.MENU_REPORT_SHIFT
			+ "') or @menuSecurity.hasMenuView('" + MenuCodeConstants.MENU_REPORT_SHIFT + "')")
	public String shiftDetail(@PathVariable Long id,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			Model model) throws Exception {

		OperatorShiftDTO shift = operatorShiftService.findById(id);
		if (shift == null) {
			return "redirect:/reports/shifts";
		}

		ParkingSessionSearchDTO searchDTO = new ParkingSessionSearchDTO();
		searchDTO.setActiveShiftId(id);

		Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
		SearchResultDTO<ParkingSessionDTO> result = sessionService.searchSessions(searchDTO, pageable);

		model.addAttribute("shift", shift);
		model.addAttribute("result", result);
		model.addAttribute("pageUrlPrefix", "/reports/shifts/" + id + "?size=" + size + "&page=");
		model.addAttribute("pageTitle", "Shift Detail");
		model.addAttribute("activeMenu", "report-shifts");
		return "reports/shift-detail";
	}
	
}