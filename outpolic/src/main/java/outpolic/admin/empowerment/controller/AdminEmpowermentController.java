package outpolic.admin.empowerment.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
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
	public String adminEmpowerView(Model model, HttpSession httpSession) {
		
		List<String> permissions = (List<String>) httpSession.getAttribute("SPermissions");
		if (!permissions.contains("SYSTEM_ADMIN")) {
			model.addAttribute("msg", "접근 권한이 없습니다.");
			model.addAttribute("url", "/admin"); // 또는 돌아갈 페이지
			return "admin/login/alert"; // alert.html이라는 공용 alert 페이지
		}
		
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











