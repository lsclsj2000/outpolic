package outpolic.admin.inquiry.service;

import java.util.List;

import outpolic.admin.inquiry.domain.AdminInquiry;


public interface AdminInquiryService {

	
	// 문의 목록 조회
	List<AdminInquiry> getAdminInquiryList();
}