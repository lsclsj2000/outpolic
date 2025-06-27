package outpolic.admin.declaration.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value="/admin")
public class AdminDeclarationController {
	
	
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
	public String adminDeclarationView() {
		// 신고 내역 조회 페이지
		
		return "admin/declaration/adminDeclarationView";
	}
}
