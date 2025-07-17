package outpolic.admin.osst.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.admin.osst.domain.AdminOsst;
import outpolic.admin.osst.service.AdminOsstService;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminOsstController {
	
	private final AdminOsstService adminOsstService; 
	
	@GetMapping("/adminOsst")
	public String adminOutsourcingStatusView(Model model) {
		// 외주 진행 목록 조회
		List<AdminOsst> adminOsst = adminOsstService.getAdminOsstList();
		
		model.addAttribute("title", "외주 진행 현황");
		model.addAttribute("adminOsst", adminOsst);
		
		return "admin/osst/adminOutsourcingStatusView";
	}
	
}
