package outpolic.user.inquiry.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import outpolic.user.inquiry.domain.UserInquiry;
import outpolic.user.inquiry.domain.UserInquiryType;


public interface UserInquiryService {
	
	// 문의첨부파일 삭제
	//void deleteUserInquiryFile(UserInquiryFile userInquiryFile);
		
	// 문의 등록
	void adduserInquiryWrite(UserInquiry inquiry, MultipartFile[] attachmentFile);
	
	// 모든 문의 유형 조회
	List<UserInquiryType> getAllInquiryTypes();
	
	// 문의 타입 조회
	List<UserInquiry> getUserInquiryTypeByCode(String inquiryTypeCode);
	
	
	// 문의 상세내용 조회
	UserInquiry getUserInquiryByCode(String inquiryCode);
	
	// 문의 목록 조회
	List<UserInquiry> getUserInquiryList();
}