package outpolic.enter.osst.service.Impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.enter.osst.domain.EnterOsst;
import outpolic.enter.osst.domain.EnterOsstRecord;
import outpolic.enter.osst.domain.EnterStepData;
import outpolic.enter.osst.mapper.EnterOsstMapper;
import outpolic.enter.osst.service.EnterOsstService;



@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class EnterOsstServiceImpl implements EnterOsstService {
	
	private final EnterOsstMapper enterOsstMapper;
	
	@Override
	public List<EnterOsstRecord> getEnterOsstRecord() {
		// 진행 외주 기록 조회
		List<EnterOsstRecord> osstRecord = enterOsstMapper.getEnterOsstRecord();
		return osstRecord;
	}

	@Override
	public EnterOsst getEnterOsstDetail(String osstDetailCode) {
		// 진행 외주 상세 조회
		EnterOsst EnterOsstDetail = enterOsstMapper.getEnterOsstDetail(osstDetailCode);
		return EnterOsstDetail;
	}
	
	@Override
	public List<EnterOsst> getEnterOsstList() {
		// 진행 외주 목록 조회
		List<EnterOsst> osstList = enterOsstMapper.getEnterOsstList();
		return osstList;
	}

	@Override
	public List<EnterOsst> getEnterOsstStcCode(String ocdCd) {
		// 진행 외주 단계 조회
		List<EnterOsst> osstStcCd = enterOsstMapper.getEnterOsstStcCode(ocdCd);
		
		return osstStcCd;
	}
	
	@Override
	public List<EnterStepData> getGroupedStepData(String ocdCd) {
		List<EnterOsst> stepList = enterOsstMapper.getEnterOsstStcCode(ocdCd); // 단계 정보
		List<EnterOsstRecord> recordList = enterOsstMapper.getEnterOsstRecord(ocdCd); // 기록 정보

		List<EnterStepData> grouped = new ArrayList<>();

		for (EnterOsst step : stepList) {
			EnterStepData stepData = new EnterStepData();
			stepData.setStcCd(step.getStcCode());
			stepData.setStcNm(step.getStcName());
			stepData.setOspSplyYmdt(step.getOspSplyYmdt());
			stepData.setOspCustYn(step.getOspCustYn());
			stepData.setOspCustYmdt(step.getOspCustYmdt());

			// ✅ 공급자 보고서 필터
			List<EnterOsstRecord> reports = recordList.stream()
				.filter(r -> step.getStcCode().equals(r.getOsstRecordStcCode()))
				.filter(r -> "SUPPLIER".equalsIgnoreCase(r.getOsstRecordType()))
				.collect(Collectors.toList());

			// ✅ 수요자 피드백 필터
			List<EnterOsstRecord> feedbacks = recordList.stream()
				.filter(r -> step.getStcCode().equals(r.getOsstRecordStcCode()))
				.filter(r -> "CUSTOMER".equalsIgnoreCase(r.getOsstRecordType()))
				.collect(Collectors.toList());

			stepData.setReports(reports);
			stepData.setFeedbacks(feedbacks);

			grouped.add(stepData);
		}

		return grouped;
	}


}
