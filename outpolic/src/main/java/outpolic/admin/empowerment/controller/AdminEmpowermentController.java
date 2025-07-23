package outpolic.admin.empowerment.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.admin.empowerment.dto.AdminEmpowermentDTO;
import outpolic.admin.empowerment.service.AdminEmpowermentService;

@Controller
@Slf4j
@RequiredArgsConstructor
public class AdminEmpowermentController {
	
	private final AdminEmpowermentService adminEmpowermentService;
	
	@GetMapping("/admin/empowerment")
	public String adminEmpowerView(Model model) {
		List<AdminEmpowermentDTO> adminList = adminEmpowermentService.AdminEmpowermentSelect();
		
		model.addAttribute("adminList", adminList);
		
		return "admin/empowerment/adminEmpowermentView";
	}
}
