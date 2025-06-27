package outpolic.user.inquiry.service;

import java.util.List;

import outpolic.user.inquiry.domain.UserInquiry;

public interface UserInquiryService {
	
	// 문의 상세내용 조회
	UserInquiry getUserInquiryByCode(String inquiryCode);

	
	// 문의 목록 조회
	List<UserInquiry> getUserInquiryList();
}
