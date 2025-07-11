package outpolic.admin.limits.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.admin.limits.domain.AdminLimits;
import outpolic.admin.limits.service.AdminLimitsService;

@Controller
@RequiredArgsConstructor
@RequestMapping(value="/admin")
@Slf4j
public class AdminLimitsController {
	
	private final AdminLimitsService adminLimitsService;
	
	@GetMapping("/adminLimitsAuthority")
	public String adminLimitsAuthorityView(Model model) {
		// 권한 설정
		List<AdminLimits> adminLimitsAuthorityList = adminLimitsService.getAdminLimitsAuthorityList();
		model.addAttribute("title", "회원 권한 조회");
		model.addAttribute("adminLimitsAuthorityList", adminLimitsAuthorityList);
		
		return "admin/limits/adminLimitsAuthorityView";
	}
	
	@GetMapping("/adminLimitsProcess")
	public String adminLimitsProcessView(Model model) {
		// 제재 처리 페이지
		model.addAttribute("title", "제재 처리");
		return "admin/limits/adminLimitsProcessView";
	}
	
	@GetMapping("/adminLimitsResources")
	public String adminLimitsResourcesView(Model model) {
		// 제재 자원 등록 페이지
		List<AdminLimits> adminLimitsTypeList = adminLimitsService.getAdminLimitsTypeList();
		model.addAttribute("title", "제재 자원 등록");
		model.addAttribute("adminLimitsTypeList", adminLimitsTypeList);
		
		return "admin/limits/adminLimitsResourcesView";
	}
	
	@GetMapping("/adminLimits")
	public String adminLimitsView(Model model) {
		// 제재 내역 조회 페이지
		List<AdminLimits> adminLimitsList = adminLimitsService.getAdminLimitsList();
		model.addAttribute("title", "제재 내역 목록");
		model.addAttribute("adminLimitsList", adminLimitsList);
		
		return "admin/limits/adminLimitsView";
	}
}
