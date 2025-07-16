package outpolic.admin.inquiry.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import outpolic.admin.inquiry.domain.AdminInquiryStatus;
import outpolic.admin.inquiry.service.AdminInquiryStatusService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminInquiryStatusController {
	private final AdminInquiryStatusService statusService;
	
	@GetMapping("/inquiryType")
	public List<AdminInquiryStatus> getInquiryType() {
		// 관리자 문의 상세-문의 타입
		List<AdminInquiryStatus> inquiryType =  statusService.getInquiryType();
		
		return inquiryType;
	}
	
	@GetMapping("/inquiryStatus")
	public List<AdminInquiryStatus> getInquiryStatus() {
		// 관리자 문의 상세-처리 상태
		List<AdminInquiryStatus> inquiryStatus =  statusService.getInquiryStatus();
		
		return inquiryStatus;
	}


}
