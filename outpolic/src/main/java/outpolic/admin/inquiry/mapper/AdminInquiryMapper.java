package outpolic.admin.inquiry.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import outpolic.admin.inquiry.domain.AdminInquiry;


@Mapper
public interface AdminInquiryMapper {
	
	// 문의 내역 수정 팝업 조회
	List<AdminInquiry> getAdminInquiryMdfcnList();
	
	// 문의 목록 조회
	List<AdminInquiry> getAdminInquiryList();
}