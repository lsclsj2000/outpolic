package outpolic.admin.inquiry.service.Impl;

import java.util.List;
import java.util.Map;

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
		AdminInquiry adminInquiryMdfcn = adminInquiryMapper.getAdminInquiryMdfcnList(inquiryCode);
		return adminInquiryMdfcn;
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

	@Override
	public void updateInquiry(AdminInquiry adminInquiry) {
	    int count1 = adminInquiryMapper.updateInquiryTable(adminInquiry);
	    int count2 = adminInquiryMapper.updateInquiryProcessTable(adminInquiry);

	    // 존재하지 않으면 insert
	    if (count2 == 0) {
	        adminInquiryMapper.insertInquiryProcessIfNotExists(adminInquiry);
	    }

	    System.out.println("🟢 update count: inquiry = " + count1 + ", process = " + count2);
	}

	@Override
	public void updateInquiryAnswer(AdminInquiry adminInquiry) {
		// 문의답변 저장
		adminInquiryMapper.updateInquiryAnswer(adminInquiry);
	}

	@Override
	public void insertInquiryType(AdminInquiryType inquiryType) {
		// 문의 자원 등록 프로세스
		adminInquiryMapper.insertInquiryType(inquiryType);
		
	}

	@Override
	public AdminInquiryType getAdminInquiryTypeByCode(String code) {
		// 문의 자원 수정_원본 조회
		return adminInquiryMapper.getAdminInquiryTypeByCode(code);
	}

	@Override
	public void updateInquiryType(AdminInquiryType inquiryType) {
		// 문의 자원 수정
		adminInquiryMapper.updateInquiryType(inquiryType);
	}
	
	@Override
	public List<AdminInquiry> getFilteredInquiryList(Map<String, Object> paramMap) {
		// 문의 필터
	    return adminInquiryMapper.getFilteredInquiryList(paramMap);
	}
	
	@Override
	public List<AdminInquiryType> getFilteredInquiryTypeList(Map<String, Object> paramMap) {
		// 문의 자원 필터
	    return adminInquiryMapper.getFilteredInquiryTypeList(paramMap);
	}


}
