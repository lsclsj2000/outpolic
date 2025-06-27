package outpolic.user.inquiry.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import outpolic.user.inquiry.domain.UserInquiry;
import outpolic.user.inquiry.service.UserInquiryService;

@Controller
@RequestMapping(value="/user")
@RequiredArgsConstructor
public class UserInquiryController {
	
	private final UserInquiryService inquiryService;
	
	
	
	@GetMapping("/userInquiryDetail")
	public String userInquiryDetailView(@RequestParam("iq_cd") String inquiryCode, Model model) {
	    UserInquiry detail = inquiryService.getUserInquiryByCode(inquiryCode);
	    model.addAttribute("inquiry", detail);
	    return "user/inquiry/userInquiryDetailView";
	}

	
	@GetMapping("/userInquiryList")
	public String userInquiryListView(Model model) {
		
		// 문의 목록 조회
		var inquiryList = inquiryService.getUserInquiryList();
		
		model.addAttribute("title", "문의");
		model.addAttribute("inquiryList", inquiryList);
		
		return "user/inquiry/userInquiryListView";
	}
	
	@GetMapping("/userInquiryNotice")
	public String userInquiryNoticeView() {
		
		return "user/inquiry/userInquiryNoticeView";
	}
	
	@GetMapping("/userInquiryTotal")
	public String userInquiryTotalView() {
		
		return "user/inquiry/userInquiryTotalView";
	}
	
	@GetMapping("/userInquiryFaq")
	public String userInquiryFaqView() {
		
		return "user/inquiry/userInquiryFqaView";
	}
	
	@GetMapping("/userInquiryWrite")
	public String userInquiryWriteView() {
		
		return "user/inquiry/userInquiryWriteView";
	}
}
