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

	    System.out.println("🟢 update count: inquiry = " + count1 + ", process = " + count2);

	    // 혹시 두 update 중 하나라도 실패했는지 로그로 확인
	    if (count1 == 0) {
	        System.err.println("⚠️ [inquiry] 테이블 업데이트 실패: " + adminInquiry.getInquiryCode());
	    }
	    if (count2 == 0) {
	        System.err.println("⚠️ [inquiry_process] 테이블 업데이트 실패: " + adminInquiry.getInquiryCode());
	    }
	}

	@Override
	public void updateInquiryAnswer(AdminInquiry adminInquiry) {
		// 문의답변 저장
		adminInquiryMapper.updateInquiryAnswer(adminInquiry);
	}

}
