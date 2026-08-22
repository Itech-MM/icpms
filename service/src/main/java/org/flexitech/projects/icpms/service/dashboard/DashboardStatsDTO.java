package org.flexitech.projects.icpms.service.dashboard;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStatsDTO {
	private long totalSites;
	private long totalGates;
	private long totalSlots;
	private long availableSlots;
	private long occupiedSlots;
	private long activeSessions;
	private long totalMembers;
	private long totalVehicles;
	private BigDecimal todayRevenue;
}
