package outpolic.user.inquiry.service;

import java.util.List;

import outpolic.user.inquiry.domain.UserInquiry;
import outpolic.user.inquiry.domain.UserInquiryAttachment;
import outpolic.user.inquiry.domain.UserInquiryProcess;
import outpolic.user.inquiry.domain.UserInquiryType;


public interface UserInquiryService {
		
	// 문의 첨부파일 등록
	void adduserInquiryAttachment(UserInquiryAttachment attachment);
	
	// 문의 등록
	void adduserInquiryWrite(UserInquiry inquiry);
	
	// 모든 문의 유형 조회
	List<UserInquiryType> getAllInquiryTypes();
	
	// 문의 타입 조회
	List<UserInquiry> getUserInquiryTypeByCode(String inquiryTypeCode);
	
	// 문의 상세내용 조회
	UserInquiry getUserInquiryByCode(String inquiryCode);
	
	// 문의 목록 조회
	List<UserInquiry> getUserInquiryList();
}