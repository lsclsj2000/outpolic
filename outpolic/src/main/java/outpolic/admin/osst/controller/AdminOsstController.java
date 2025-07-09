package outpolic.admin.osst.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminOsstController {
	
	@GetMapping("/adminOsst")
	public String adminOutsourcingStatusView(Model model) {
		
		model.addAttribute("title", "외주 진행 현황");
		
		return "admin/osst/adminOutsourcingStatusView";
	}
	
}
