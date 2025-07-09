package outpolic.enter.inquiry.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import outpolic.enter.inquiry.domain.EnterInquiry;
import outpolic.enter.inquiry.domain.EnterInquiryType;
import outpolic.user.inquiry.domain.UserInquiry;


public interface EnterInquiryService {
	
	// 문의첨부파일 삭제
	//void deleteUserInquiryFile(UserInquiryFile userInquiryFile);
		
	// 문의 등록
	void addenterInquiryWrite(EnterInquiry inquiry, MultipartFile[] attachmentFile);
	
	// 모든 문의 유형 조회
	List<EnterInquiryType> getAllInquiryTypes();
	
	// 문의 타입 조회
	List<EnterInquiry> getEnterInquiryTypeByCode(String inquiryTypeCode);
	
	// 문의 상세내용 조회
	EnterInquiry getEnterInquiryByCode(String inquiryCode);
	
	// 문의 목록 조회
	List<EnterInquiry> getEnterInquiryList();
}