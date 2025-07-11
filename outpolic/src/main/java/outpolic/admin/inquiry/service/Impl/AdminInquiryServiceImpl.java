package outpolic.admin.inquiry.service.Impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.admin.inquiry.domain.AdminInquiry;
import outpolic.admin.inquiry.domain.AdminInquiryType;
import outpolic.admin.inquiry.mapper.AdminInquiryMapper;
import outpolic.admin.inquiry.mapper.AdminInquiryMapper;
import outpolic.admin.inquiry.service.AdminInquiryService;



@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AdminInquiryServiceImpl implements AdminInquiryService {
	
	private final AdminInquiryMapper adminInquiryMapper;

	@Override
	public AdminInquiry getAdminInquiryMdfcnList(String inquiryCode) {
		// 문의 상세 수정 팝업창
		AdminInquiry admininquiry = adminInquiryMapper.getAdminInquiryMdfcnList(inquiryCode);
		return admininquiry;
	}
	
	@Override
	public List<AdminInquiry> getAdminInquiryList() {
		// 문의 목록 조회
		List<AdminInquiry> adminInquiryList = adminInquiryMapper.getAdminInquiryList();
		return adminInquiryList;
	}

	@Override
	public List<AdminInquiryType> getAdminInquiryTypeList() {
		// 문의 타입 자원 조회
		List<AdminInquiryType> adminInquiryTypeList = adminInquiryMapper.getAdminInquiryTypeList();
		return adminInquiryTypeList;
	}

}
