package org.flexitech.projects.icpms.admin.controller;

import org.flexitech.projects.icpms.common.MenuCodeConstants;
import org.flexitech.projects.icpms.service.dashboard.DashboardService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	private final DashboardService dashboardService;

	public HomeController(DashboardService dashboardService) {
		this.dashboardService = dashboardService;
	}

	@GetMapping("/")
	@PreAuthorize("@menuSecurity.hasMenuAccess('" + MenuCodeConstants.MENU_DASHBOARD
			+ "') or @menuSecurity.hasMenuView('" + MenuCodeConstants.MENU_DASHBOARD + "')")
	public String dashboard(Model model) {
		model.addAttribute("stats", dashboardService.getStats());
		model.addAttribute("pageTitle", "Dashboard");
		model.addAttribute("activeMenu", "dashboard");
		return "dashboard";
	}
}