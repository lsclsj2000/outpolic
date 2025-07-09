package outpolic.admin.inquiry.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import outpolic.admin.inquiry.domain.AdminInquiry;
import outpolic.admin.inquiry.domain.AdminInquiryType;
import outpolic.admin.inquiry.service.AdminInquiryService;

@Controller
@RequiredArgsConstructor
@RequestMapping(value="/admin")
public class AdminInquiryController {
	
	private final AdminInquiryService adminInquiryService; 
	
	
	 @GetMapping("/adminInquiryResources") 
	 public String adminInquiryResourcesView(Model model) {
		 // 문의 자원 등록 페이지
		 List<AdminInquiryType> adminInquiryTypeList = adminInquiryService.getAdminInquiryTypeList();
		 
		 model.addAttribute("title", "문의 자원 등록");
		 model.addAttribute("adminInquiryTypeList", adminInquiryTypeList);
		 
		 return "admin/inquiry/adminInquiryResourcesView"; }
	 
	 
//	 @GetMapping("/adminInquiryProcess") public String adminInquiryProcessView() {
//	 // 문의 처리 페이지
//	 
//	 return "admin/inquiry/adminInquiryProcessView"; }
	 
	 
	
	@GetMapping("/adminInquiryMdfcn")
	@ResponseBody
	public AdminInquiry adminInquiryMdfcn(@RequestParam("inquiryCode") String inquiryCode) {
		// 문의 상세 수정 팝업창
		
		return adminInquiryService.getAdminInquiryMdfcnList(inquiryCode);
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
