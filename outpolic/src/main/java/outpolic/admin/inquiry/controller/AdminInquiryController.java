package outpolic.admin.inquiry.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value="/admin")
public class AdminInquiryController {
	
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
	
	@GetMapping("/adminInquiry")
	public String adminInquiryView() {
		// 문의내역 조회 페이지
		
		return "admin/inquiry/adminInquiryView";
	}
}
