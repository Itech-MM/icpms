package org.flexitech.projects.icpms.service.dashboard;

import java.math.BigDecimal;

import org.flexitech.projects.icpms.common.CommonConstants;
import org.flexitech.projects.icpms.common.enums.ParkingSessionStatus;
import org.flexitech.projects.icpms.common.enums.SlotStatus;
import org.flexitech.projects.icpms.common.utils.DateUtils;
import org.flexitech.projects.icpms.dto.payment.PaymentSearchDTO;
import org.flexitech.projects.icpms.persistence.repositories.gate.GateRepository;
import org.flexitech.projects.icpms.persistence.repositories.member.MemberRepository;
import org.flexitech.projects.icpms.persistence.repositories.session.ParkingSessionRepository;
import org.flexitech.projects.icpms.persistence.repositories.site.SiteRepository;
import org.flexitech.projects.icpms.persistence.repositories.slot.ParkingSlotRepository;
import org.flexitech.projects.icpms.persistence.repositories.vehicle.VehicleRepository;
import org.flexitech.projects.icpms.service.payment.PaymentService;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class DashboardServiceImpl implements DashboardService {

	private final SiteRepository siteRepository;
	private final GateRepository gateRepository;
	private final ParkingSlotRepository slotRepository;
	private final MemberRepository memberRepository;
	private final VehicleRepository vehicleRepository;
	private final ParkingSessionRepository sessionRepository;
	private final PaymentService paymentService;

	public DashboardServiceImpl(SiteRepository siteRepository, GateRepository gateRepository,
			ParkingSlotRepository slotRepository, MemberRepository memberRepository,
			VehicleRepository vehicleRepository, ParkingSessionRepository sessionRepository,
			PaymentService paymentService) {
		this.siteRepository = siteRepository;
		this.gateRepository = gateRepository;
		this.slotRepository = slotRepository;
		this.memberRepository = memberRepository;
		this.vehicleRepository = vehicleRepository;
		this.sessionRepository = sessionRepository;
		this.paymentService = paymentService;
	}

	@Override
	public DashboardStatsDTO getStats() {
		DashboardStatsDTO stats = new DashboardStatsDTO();
		stats.setTotalSites(siteRepository.count());
		stats.setTotalGates(gateRepository.count());
		stats.setTotalSlots(slotRepository.count());
		stats.setAvailableSlots(slotRepository.countByStatus(SlotStatus.AVAILABLE.getCode()));
		stats.setOccupiedSlots(slotRepository.countByStatus(SlotStatus.OCCUPIED.getCode()));
		stats.setActiveSessions(sessionRepository.countByStatus(ParkingSessionStatus.ACTIVE.getCode()));
		stats.setTotalMembers(memberRepository.count());
		stats.setTotalVehicles(vehicleRepository.count());

		PaymentSearchDTO todaySearch = new PaymentSearchDTO();
		String today = DateUtils.dateToString(new Date(), CommonConstants.STANDARD_DB_DATE_FORMAT);
		todaySearch.setFromDate(today);
		todaySearch.setToDate(today);
		BigDecimal revenue = paymentService.sumAmount(todaySearch);
		stats.setTodayRevenue(revenue == null ? BigDecimal.ZERO : revenue);

		return stats;
	}
}
