package outpolic.user.inquiry.service.Impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import outpolic.user.inquiry.domain.UserInquiry;
import outpolic.user.inquiry.mapper.UserInquiryMapper;
import outpolic.user.inquiry.service.UserInquiryService;

@Service
@Transactional
@RequiredArgsConstructor
public class UserInquiryServiceImpl implements UserInquiryService {
	
	private final UserInquiryMapper inquiryMapper;
	
	@Override
	public UserInquiry getUserInquiryByCode(String inquiryCode) {
		
		// 문의 상세내용 조회
	    return inquiryMapper.getUserInquiryByCode(inquiryCode);
	}

	
	@Override
	public List<UserInquiry> getUserInquiryList() {
		
		// 회원 목록 조회
		List<UserInquiry> inquiryList = inquiryMapper.getUserInquiryList();
		return inquiryList;
	}

}
