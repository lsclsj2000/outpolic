package outpolic.enter.inquiry.service;

import java.util.List;

import outpolic.enter.inquiry.domain.EnterInquiry;

public interface EnterInquiryService {
	
	// 문의 상세내용 조회
	EnterInquiry getEnterInquiryByCode(String inquiryCode);

	
	// 문의 목록 조회
	List<EnterInquiry> getEnterInquiryList();
}
