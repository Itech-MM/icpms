package org.flexitech.projects.icpms.admin.controller;

import org.flexitech.projects.icpms.service.dashboard.DashboardService;
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
	public String dashboard(Model model) {
		model.addAttribute("stats", dashboardService.getStats());
		model.addAttribute("pageTitle", "Dashboard");
		model.addAttribute("activeMenu", "dashboard");
		return "dashboard";
	}
}
