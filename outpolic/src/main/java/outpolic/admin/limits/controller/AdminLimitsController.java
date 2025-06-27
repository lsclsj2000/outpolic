package outpolic.admin.limits.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value="/admin")
public class AdminLimitsController {
	
	@GetMapping("/adminLimitsProcess")
	public String adminLimitsProcessView() {
		// 제재 처리
		
		return "admin/limits/adminLimitsProcessView";
	}
	
	@GetMapping("/adminLimitsManage")
	public String adminLimitsManageView() {
		// 제재 프로세스 관리 페이지
		
		return "admin/limits/adminLimitsManageView";
	}
	
	@GetMapping("/adminLimits")
	public String adminLimitsView() {
		// 제재 내역 조회 페이지
		
		return "admin/limits/adminLimitsView";
	}
}
