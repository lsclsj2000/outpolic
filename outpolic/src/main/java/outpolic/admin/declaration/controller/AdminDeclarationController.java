package outpolic.admin.declaration.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import outpolic.admin.declaration.domain.AdminDeclaration;
import outpolic.admin.declaration.service.AdminDeclarationService;

@Controller
@RequiredArgsConstructor
@RequestMapping(value="/admin")
public class AdminDeclarationController {
	
	private final AdminDeclarationService adminDeclarationService;
	
	@GetMapping("/adminDeclarationManage")
	public String adminDeclarationManageView() {
		// 신고 프로세스 등록 페이지
		
		return "admin/declaration/adminDeclarationManageView";
	}
	
	@GetMapping("/adminDeclarationProcess")
	public String adminDeclarationProcessView() {
		// 신고 내역 조회 페이지
		
		return "admin/declaration/adminDeclarationProcessView";
	}
	
	@GetMapping("/adminDeclaration")
	public String adminDeclarationView(Model model) {
		// 신고 내역 조회 페이지
		List<AdminDeclaration> adminDeclarationList = adminDeclarationService.getAdminDeclarationList();
		
		model.addAttribute("title", "신고 내역 조회");
		model.addAttribute("adminDeclarationList", adminDeclarationList);
		
		return "admin/declaration/adminDeclarationView";
	}
}
