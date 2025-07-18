package outpolic.admin.dashboard.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import outpolic.admin.dashboard.domain.AdminDashboardDTO;
import outpolic.admin.dashboard.service.AdminDashboardService;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminDashboardApiController {

	private final AdminDashboardService dashboardService;

    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardDTO> getDashboardData() {
        AdminDashboardDTO summary = dashboardService.getDashboardSummary();
        return ResponseEntity.ok(summary);
    }
}
