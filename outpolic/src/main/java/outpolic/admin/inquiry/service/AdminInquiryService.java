package outpolic.admin.inquiry.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.repository.query.Param;

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
	
	// 문의 필터
	List<AdminInquiry> getFilteredInquiryList(Map<String, Object> paramMap);
	
	// 문의 자원 필터
	List<AdminInquiryType> getFilteredInquiryTypeList(Map<String, Object> paramMap);
	
	// 문의 수 카운트
 	int getAdminInquiryListCount();
	 	
 	// 문의 리스트 페이지네이션 위한 리스트 호출
 	List<AdminInquiry> getAdminInquiryListForPg(@Param("startRow") int startRow, @Param("rowPerPage") int rowPerPage);
	    
	
}