package outpolic.enter.inquiry.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import outpolic.enter.inquiry.domain.EnterInquiry;

@Mapper
public interface EnterInquiryMapper {
	
	// 문의 상세내용 조회
	EnterInquiry getEnterInquiryByCode(String inquiryCode);
	
	// 문의 목록 조회
	List<EnterInquiry> getInquiryList();
}
