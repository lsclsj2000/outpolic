package outpolic.enter.inquiry.service.Impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import outpolic.enter.inquiry.domain.EnterInquiry;
import outpolic.enter.inquiry.mapper.EnterInquiryMapper;
import outpolic.enter.inquiry.service.EnterInquiryService;

@Service
@Transactional
@RequiredArgsConstructor
public class EnterInquiryServiceImpl implements EnterInquiryService {
	
	private final EnterInquiryMapper inquiryMapper;
	
	@Override
	public EnterInquiry getEnterInquiryByCode(String inquiryCode) {
		
		// 문의 상세내용 조회
	    return inquiryMapper.getEnterInquiryByCode(inquiryCode);
	}

	
	@Override
	public List<EnterInquiry> getEnterInquiryList() {
		
		// 회원 목록 조회
		List<EnterInquiry> inquiryList = inquiryMapper.getInquiryList();
		return inquiryList;
	}

}
