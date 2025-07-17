package outpolic.admin.inquiry.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import outpolic.admin.inquiry.domain.AdminInquiryStatus;

@Mapper
public interface AdminInquiryStatusMapper {
	
	// 관리자 문의 상세-문의 타입
	List<AdminInquiryStatus> getInquiryType();
	
	// 관리자 문의 상세-처리 상태
	List<AdminInquiryStatus> getInquiryStatus();
}
