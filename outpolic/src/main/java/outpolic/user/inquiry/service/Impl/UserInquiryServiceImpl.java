package outpolic.user.inquiry.service.Impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import lombok.RequiredArgsConstructor;
import outpolic.user.inquiry.domain.UserInquiry;
import outpolic.user.inquiry.domain.UserInquiryAttachment;
import outpolic.user.inquiry.domain.UserInquiryProcess;
import outpolic.user.inquiry.domain.UserInquiryType;
import outpolic.user.inquiry.mapper.UserInquiryMapper;
import outpolic.user.inquiry.service.UserInquiryService;



@Service
@Transactional
@RequiredArgsConstructor
public class UserInquiryServiceImpl implements UserInquiryService {
	
	private final UserInquiryMapper inquiryMapper;
	
	
	
	@Override
	public void adduserInquiryAttachment(UserInquiryAttachment attachment) {
		// 문의 첨부파일 등록
		inquiryMapper.adduserInquiryAttachment(attachment);
		
	}
	
	
	@Override
	public void adduserInquiryWrite(UserInquiry inquiry) {
		// 문의 등록
		inquiryMapper.adduserInquiryWrite(inquiry);
	}
	
	
	@Override
	public List<UserInquiry> getUserInquiryTypeByCode(String inquiryTypeCode) {
	
		// 문의 타입 조회	
		return inquiryMapper.getUserInquiryTypeByCode(inquiryTypeCode);
	}

	
	@Override
	public UserInquiry getUserInquiryByCode(String inquiryCode) {
		// 문의 상세내용 조회
		return inquiryMapper.getUserInquiryByCode(inquiryCode);
	}

	
	@Override
	public List<UserInquiry> getUserInquiryList() {
		// 문의 목록 조회
		List<UserInquiry> inquiryList = inquiryMapper.getUserInquiryList();
		return inquiryList;
	}

	
	@Override
	public List<UserInquiryType> getAllInquiryTypes() {
		// 문의 타입 조회
		return inquiryMapper.getAllInquiryTypes();
	}
}
