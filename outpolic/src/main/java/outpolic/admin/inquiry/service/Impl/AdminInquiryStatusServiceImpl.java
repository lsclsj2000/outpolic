package outpolic.admin.inquiry.service.Impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.admin.inquiry.domain.AdminInquiryStatus;
import outpolic.admin.inquiry.mapper.AdminInquiryStatusMapper;
import outpolic.admin.inquiry.service.AdminInquiryStatusService;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AdminInquiryStatusServiceImpl implements AdminInquiryStatusService {
	
	private final AdminInquiryStatusMapper statusMapper;

	@Override
	public List<AdminInquiryStatus> getInquiryType() {
		// 관리자 문의 상세-문의 타입
		List<AdminInquiryStatus> inquiryType =  statusMapper.getInquiryType();
		return inquiryType;
	}
		
	@Override
	public List<AdminInquiryStatus> getInquiryStatus() {
		// 관리자 문의 상세-처리 상태
		List<AdminInquiryStatus> inquiryStatus =  statusMapper.getInquiryStatus();
		return inquiryStatus;
	}


}
