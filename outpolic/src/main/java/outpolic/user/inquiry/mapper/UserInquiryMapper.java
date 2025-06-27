package outpolic.user.inquiry.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import outpolic.user.inquiry.domain.UserInquiry;

@Mapper
public interface UserInquiryMapper {
	
	// 문의 상세내용 조회
	UserInquiry getUserInquiryByCode(String inquiryCode);
	
	// 문의 목록 조회
	List<UserInquiry> getUserInquiryList();
}
