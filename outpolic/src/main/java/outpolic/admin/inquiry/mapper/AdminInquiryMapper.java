package outpolic.admin.inquiry.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import outpolic.admin.inquiry.domain.AdminInquiry;
import outpolic.admin.inquiry.domain.AdminInquiryType;


@Mapper
public interface AdminInquiryMapper {
	
	// 문의 타입 자원 조회
	List<AdminInquiryType> getAdminInquiryTypeList();
	
	// 문의 상세 수정 팝업창
	AdminInquiry getAdminInquiryMdfcnList(String inquiryCode);
	
	// 문의 목록 조회
	List<AdminInquiry> getAdminInquiryList();
	
	// 문의 테이블 수정
    int updateInquiryTable(AdminInquiry adminInquiry);

    // 문의 프로세스 테이블 수정
    int updateInquiryProcessTable(AdminInquiry adminInquiry);
    
    // 문의답변 저장
    void updateInquiryAnswer(AdminInquiry adminInquiry);
}