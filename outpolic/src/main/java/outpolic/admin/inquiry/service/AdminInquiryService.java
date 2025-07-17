package outpolic.admin.inquiry.service;

import java.util.List;

import outpolic.admin.inquiry.domain.AdminInquiry;
import outpolic.admin.inquiry.domain.AdminInquiryType;


public interface AdminInquiryService {
	
	// 문의 자원 수정
	void updateInquiryType(AdminInquiryType inquiryType);
	
	// 문의 자원 수정_원본 조회
	AdminInquiryType getAdminInquiryTypeByCode(String code);
	
	// 문의 자원 등록 프로세스
	void insertInquiryType(AdminInquiryType inquiryType);
	
	// 문의 타입 자원 조회
	List<AdminInquiryType> getAdminInquiryTypeList();

	// 문의 상세 수정 팝업창
	AdminInquiry getAdminInquiryMdfcnList(String inquiryCode);
	
	// 문의 목록 조회
	List<AdminInquiry> getAdminInquiryList();
	
	// 문의내역 수정
	public void updateInquiry(AdminInquiry adminInquiry);
	
	// 문의답변 저장
	void updateInquiryAnswer(AdminInquiry adminInquiry);
}