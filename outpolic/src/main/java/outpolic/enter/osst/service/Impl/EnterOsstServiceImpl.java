package outpolic.enter.osst.service.Impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
	public EnterOsst getEnterOsstDetail(String osstDetailCode, String memberCode) {
	    return enterOsstMapper.getEnterOsstDetail(osstDetailCode, memberCode);
	}
	
	@Override
	public List<EnterOsst> getEnterOsstList(String memberCode) {
		// 진행 외주 목록 조회
		List<EnterOsst> osstList = enterOsstMapper.getEnterOsstList(memberCode);
		return osstList;
	}

	@Override
	public List<EnterOsst> getEnterOsstStcCode(String ocdCd) {
		// 진행 외주 단계 조회
		List<EnterOsst> osstStcCd = enterOsstMapper.getEnterOsstStcCode(ocdCd);
		
		return osstStcCd;
	}
	
	// ocdCd 기준으로 외주 기록을 조회하는 메서드 추가 (Service 인터페이스와 일치)
	@Override
	public List<EnterOsstRecord> getOutsourcingRecordsByOcdCd(String ocdCd) {
		return enterOsstMapper.getOutsourcingRecordsByOcdCd(ocdCd);
	}

	@Override
	public List<EnterStepData> getGroupedStepData(String ocdCd) {
		List<EnterOsst> stepList = enterOsstMapper.getEnterOsstStcCode(ocdCd); // 단계 정보
		List<EnterOsstRecord> recordList = enterOsstMapper.getOutsourcingRecordsByOcdCd(ocdCd); // 기록 정보 (메서드명 변경)

		List<EnterStepData> grouped = new ArrayList<>();

		// recordList의 모든 외주 기록에 대한 상세 로그 출력
		// 이 로그는 모든 기록이 어떤 '기록 단계 코드'를 가지고 있는지 파악하는 데 도움이 됩니다.
		log.debug("===== 모든 외주 기록 (recordList) 내용 시작 =====");
		if (recordList.isEmpty()) {
		    log.debug("recordList가 비어 있습니다.");
		} else {
		    for (EnterOsstRecord r : recordList) {
		        log.debug("외주 기록 - osr_cd: {}, osp_cd: {}, osr_type: {}, 기록 단계 코드 (r.getOsstRecordStcCode()): {}", 
		                  r.getOsstRecordCode(), r.getOsstPrograssCode(), r.getOsstRecordType(), r.getOsstRecordStcCode());
		    }
		}
		log.debug("===== 모든 외주 기록 (recordList) 내용 끝 =====");


		for (EnterOsst step : stepList) {
			// 각 step을 처리하기 시작할 때 현재 단계 코드 로그 출력
			log.debug("--- 현재 단계 코드 (step.getStcCode()): {}", step.getStcCode());

			EnterStepData stepData = new EnterStepData();
			stepData.setStcCd(step.getStcCode());
			stepData.setStcNm(step.getStcName());
			stepData.setOspSplyYmdt(step.getOspSplyYmdt());
			stepData.setOspCustYn(step.getOspCustYn());
			stepData.setOspCustYmdt(step.getOspCustYmdt());
			stepData.setOspCd(step.getOspCode());

			// 공급자 보고서 필터: "SUPPLIER" 대신 "REPORT"로 변경
			List<EnterOsstRecord> reports = recordList.stream()
				.filter(r -> step.getStcCode().equals(r.getOsstRecordStcCode()))
				.filter(r -> "REPORT".equalsIgnoreCase(r.getOsstRecordType())) // <-- 이 부분을 수정했습니다.
				.collect(Collectors.toList());

			// 수요자 피드백 필터: "CUSTOMER" 대신 "FEEDBACK"로 변경
			List<EnterOsstRecord> feedbacks = recordList.stream()
				.filter(r -> step.getStcCode().equals(r.getOsstRecordStcCode()))
				.filter(r -> "FEEDBACK".equalsIgnoreCase(r.getOsstRecordType())) // <-- 이 부분을 수정했습니다.
				.collect(Collectors.toList());

			stepData.setReports(reports);
			stepData.setFeedbacks(feedbacks);

			grouped.add(stepData);
		}
		
		for (EnterStepData step : grouped) {
		    if (step.isCompleted() && step.getReports() != null && !step.getReports().isEmpty()) {
		        step.getReports().sort(Comparator.comparing(EnterOsstRecord::getOsstRecordRegYmdt));
		        EnterOsstRecord lastReport = step.getReports().get(step.getReports().size() - 1);
		        step.setFinalResults(List.of(lastReport));
		    } else {
		        step.setFinalResults(Collections.emptyList());
		    }
		}

		return grouped;
	}
	
	@Override
	public String generateNewOsrCode() {
	    String newCode = enterOsstMapper.getNextOsrCode();
	    return newCode;
	}

	@Override
	public boolean insertRecord(EnterOsstRecord record) {
	    return enterOsstMapper.insertRecord(record) == 1;
	}
	
	@Override
	public boolean approveStep(String ospCd) {
		// 단계 승인
	    return enterOsstMapper.updateStepApproval(ospCd) == 1;
	}
}