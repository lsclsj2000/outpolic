package outpolic.enter.inquiry.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import outpolic.enter.inquiry.domain.EnterInquiry;
import outpolic.enter.inquiry.service.EnterInquiryService;

@Controller
@RequestMapping(value="/enter")
@RequiredArgsConstructor
public class EnterInquiryController {
	
	private final EnterInquiryService inquiryService;
	
	
	
	@GetMapping("/enterInquiryDetail")
	public String enterInquiryDetailView(@RequestParam("iq_cd") String inquiryCode, Model model) {
	    EnterInquiry detail = inquiryService.getEnterInquiryByCode(inquiryCode);
	    model.addAttribute("inquiry", detail);
	    return "enter/inquiry/enterInquiryDetailView";
	}

	
	@GetMapping("/enterInquiryList")
	public String enterInquiryListView(Model model) {
		
		// 문의 목록 조회
		var inquiryList = inquiryService.getEnterInquiryList();
		
		model.addAttribute("title", "문의");
		model.addAttribute("inquiryList", inquiryList);
		
		return "enter/inquiry/enterInquiryListView";
	}
	
	@GetMapping("/enterInquiryNotice")
	public String enterInquiryNoticeView() {
		
		return "enter/inquiry/enterInquiryNoticeView";
	}
	
	@GetMapping("/enterInquiryTotal")
	public String enterInquiryTotalView() {
		
		return "enter/inquiry/enterInquiryTotalView";
	}
	
	@GetMapping("/enterInquiryFaq")
	public String enterInquiryFaqView() {
		
		return "enter/inquiry/enterInquiryFqaView";
	}
	
	@GetMapping("/enterInquiryWrite")
	public String enterInquiryWriteView() {
		
		return "enter/inquiry/enterInquiryWriteView";
	}
}
