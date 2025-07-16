package outpolic.admin.inquiry.service;

import java.util.List;

import outpolic.admin.inquiry.domain.AdminInquiryStatus;

public interface AdminInquiryStatusService {
	
	// 관리자 문의 상세-문의 타입
	List<AdminInquiryStatus> getInquiryType();
	
	// 관리자 문의 상세-처리 상태
	List<AdminInquiryStatus> getInquiryStatus();
}
