package outpolic.admin.inquiry.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import outpolic.admin.inquiry.domain.AdminInquiry;
import outpolic.user.inquiry.domain.UserInquiry;
import outpolic.user.inquiry.domain.UserInquiryFile;
import outpolic.user.inquiry.domain.UserInquiryProcess;
import outpolic.user.inquiry.domain.UserInquiryType;


@Mapper
public interface AdminInquiryMapper {
	
	
	// 문의 목록 조회
	List<AdminInquiry> getAdminInquiryList();
}