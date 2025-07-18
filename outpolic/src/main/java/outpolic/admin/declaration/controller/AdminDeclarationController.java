package outpolic.admin.declaration.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import outpolic.admin.declaration.domain.AdminDeclaration;
import outpolic.admin.declaration.service.AdminDeclarationService;
import outpolic.admin.inquiry.domain.AdminInquiryType;

@Controller
@RequiredArgsConstructor
@RequestMapping(value="/admin")
public class AdminDeclarationController {
	
	private final AdminDeclarationService adminDeclarationService;
	
	@PostMapping("/addDeclarationType")
	@ResponseBody
	public String addDeclarationType(@RequestBody AdminDeclaration adminDeclaration, HttpSession session) {
		// 신고 타입 등록
	    String adminCode = (String) session.getAttribute("SACD");
	    if (adminCode == null) return "FAIL: Unauthorized";
	    adminDeclaration.setDeclarationTypeRegAdmCode(adminCode);
	    adminDeclarationService.insertDeclarationType(adminDeclaration);
	    return "OK";
	}
	
	@GetMapping("/declarationTypes")
	@ResponseBody
	public List<AdminDeclaration> getDeclarationTypes() {
		// 신고 사유_상위 신고 타입
	    return adminDeclarationService.getAdminDeclarationTypeList();
	}

	@PostMapping("/addDeclarationReason")
	@ResponseBody
	public String addDeclarationReason(@RequestBody AdminDeclaration adminDeclaration, HttpSession session) {
		// 신고 사유 등록
	    String adminCode = (String) session.getAttribute("SACD");
	    if (adminCode == null) return "FAIL: Unauthorized";
	    adminDeclaration.setDrRegAdmCode(adminCode);
	    adminDeclarationService.insertDeclarationReason(adminDeclaration);
	    return "OK";
	}

	@PostMapping("/addDeclarationResult")
	@ResponseBody
	public String addDeclarationResult(@RequestBody AdminDeclaration adminDeclaration, HttpSession session) {
		// 신고처리결과 등록
	    String adminCode = (String) session.getAttribute("SACD");
	    if (adminCode == null) return "FAIL: Unauthorized";
	    adminDeclaration.setDrcRegAdmCode(adminCode);
	    adminDeclarationService.insertDeclarationResult(adminDeclaration);
	    return "OK";
	}
	
	@GetMapping("/adminDeclarationResources")
	public String adminDeclarationManageView(Model model) {
		// 신고 자원 등록 페이지
		List<AdminDeclaration> adminDeclarationTypeList = adminDeclarationService.getAdminDeclarationTypeList();
		List<AdminDeclaration> adminDeclarationReasonList  = adminDeclarationService.getAdminDeclarationReasonList();
		List<AdminDeclaration> adminDeclarationResultList  = adminDeclarationService.getAdminDeclarationResultList();
		
		model.addAttribute("title", "신고 자원 등록");
		model.addAttribute("adminDeclarationTypeList", adminDeclarationTypeList);
		model.addAttribute("adminDeclarationReasonList", adminDeclarationReasonList);
		model.addAttribute("adminDeclarationResultList", adminDeclarationResultList);
		
		return "admin/declaration/adminDeclarationResourcesView";
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
