package outpolic.enter.portfolioInquiry.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/enter")
public class PortfolioInquiryController {
	
	@GetMapping("/myPortfolioInquiryListView")
	public String myPortfolioInquiryListView() {
		return "enter/portfolioInquiry/myPortfolioInquiryListView";
	}
	
	@GetMapping("/portfolioInquiryListView")
	public String portfolioInquiryListView() {
		return "enter/portfolioInquiry/portfolioInquiryListView";
	}
	
}
