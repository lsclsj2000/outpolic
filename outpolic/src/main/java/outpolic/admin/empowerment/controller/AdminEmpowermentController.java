package outpolic.admin.empowerment.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
	
	// 권한 부여/회수
	@PostMapping("/admin/empowerment/save")
	@ResponseBody
	public String saveEmpowerment(
	        @RequestParam("admCd") String admCd,
	        @RequestParam(value = "permissions", required = false) List<String> newPermissions) {

		adminEmpowermentService.updateAdminPermissions(admCd, newPermissions);
	    return "ok";
	}
	
}











