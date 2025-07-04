package outpolic.admin.inquiry.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import outpolic.admin.inquiry.service.AdminInquiryService;

@Controller
@RequiredArgsConstructor
@RequestMapping(value="/admin")
public class AdminInquiryController {
	
	private final AdminInquiryService adminInquiryService; 
	
	@GetMapping("/adminInquiryManage")
	public String adminInquiryManageView() {
		// 문의 프로세스 관리
		
		return "admin/inquiry/adminInquiryManageView";
	}
	
	@GetMapping("/adminInquiryProcess")
	public String adminInquiryProcessView() {
		// 문의 처리 페이지
		
		return "admin/inquiry/adminInquiryProcessView";
	}
	
	@GetMapping("/adminInquiryList")
	public String adminInquiryView(Model model) {
		// 문의내역 조회 페이지
		var inquiryList = adminInquiryService.getAdminInquiryList();
		
		model.addAttribute("title", "관리자 문의 내역");
		model.addAttribute("inquiryList", inquiryList);
		
		return "admin/inquiry/adminInquiryView";
	}
}
