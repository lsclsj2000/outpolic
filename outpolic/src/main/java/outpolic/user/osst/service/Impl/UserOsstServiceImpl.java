package outpolic.user.osst.service.Impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.user.osst.domain.UserOsst;
import outpolic.user.osst.domain.UserOsstRecord;
import outpolic.user.osst.domain.UserStepData;
import outpolic.user.osst.mapper.UserOsstMapper;
import outpolic.user.osst.service.UserOsstService;



@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserOsstServiceImpl implements UserOsstService {
	
	private final UserOsstMapper userOsstMapper;
	

	@Override
	public UserOsst getUserOsstDetail(String osstDetailCode, String memberCode) {
	    return userOsstMapper.getUserOsstDetail(osstDetailCode, memberCode);
	}
	
	@Override
	public List<UserOsst> getUserOsstList(String memberCode) {
		// 진행 외주 목록 조회
		List<UserOsst> osstList = userOsstMapper.getUserOsstList(memberCode);
		return osstList;
	}

	@Override
	public List<UserOsst> getUserOsstStcCode(String ocdCd) {
		// 진행 외주 단계 조회
		List<UserOsst> osstStcCd = userOsstMapper.getUserOsstStcCode(ocdCd);
		
		return osstStcCd;
	}
	
	// ocdCd 기준으로 외주 기록을 조회하는 메서드 추가 (Service 인터페이스와 일치)
	@Override
	public List<UserOsstRecord> getOutsourcingRecordsByOcdCd(String ocdCd) {
		return userOsstMapper.getOutsourcingRecordsByOcdCd(ocdCd);
	}

	@Override
	public List<UserStepData> getGroupedStepData(String ocdCd) {
		List<UserOsst> stepList = userOsstMapper.getUserOsstStcCode(ocdCd); // 단계 정보
		List<UserOsstRecord> recordList = userOsstMapper.getOutsourcingRecordsByOcdCd(ocdCd); // 기록 정보 (메서드명 변경)

		List<UserStepData> grouped = new ArrayList<>();

		// recordList의 모든 외주 기록에 대한 상세 로그 출력
		// 이 로그는 모든 기록이 어떤 '기록 단계 코드'를 가지고 있는지 파악하는 데 도움이 됩니다.
		log.debug("===== 모든 외주 기록 (recordList) 내용 시작 =====");
		if (recordList.isEmpty()) {
		    log.debug("recordList가 비어 있습니다.");
		} else {
		    for (UserOsstRecord r : recordList) {
		        log.debug("외주 기록 - osr_cd: {}, osp_cd: {}, osr_type: {}, 기록 단계 코드 (r.getOsstRecordStcCode()): {}", 
		                  r.getOsstRecordCode(), r.getOsstPrograssCode(), r.getOsstRecordType(), r.getOsstRecordStcCode());
		    }
		}
		log.debug("===== 모든 외주 기록 (recordList) 내용 끝 =====");


		for (UserOsst step : stepList) {
			// 각 step을 처리하기 시작할 때 현재 단계 코드 로그 출력
			log.debug("--- 현재 단계 코드 (step.getStcCode()): {}", step.getStcCode());

			UserStepData stepData = new UserStepData();
			stepData.setStcCd(step.getStcCode());
			stepData.setStcNm(step.getStcName());
			stepData.setOspSplyYmdt(step.getOspSplyYmdt());
			stepData.setOspCustYn(step.getOspCustYn());
			stepData.setOspCustYmdt(step.getOspCustYmdt());
			stepData.setOspCd(step.getOspCode());

			// 공급자 보고서 필터: "SUPPLIER" 대신 "REPORT"로 변경
			List<UserOsstRecord> reports = recordList.stream()
				.filter(r -> step.getStcCode().equals(r.getOsstRecordStcCode()))
				.filter(r -> "REPORT".equalsIgnoreCase(r.getOsstRecordType())) // <-- 이 부분을 수정했습니다.
				.collect(Collectors.toList());

			// 수요자 피드백 필터: "CUSTOMER" 대신 "FEEDBACK"로 변경
			List<UserOsstRecord> feedbacks = recordList.stream()
				.filter(r -> step.getStcCode().equals(r.getOsstRecordStcCode()))
				.filter(r -> "FEEDBACK".equalsIgnoreCase(r.getOsstRecordType())) // <-- 이 부분을 수정했습니다.
				.collect(Collectors.toList());

			stepData.setReports(reports);
			stepData.setFeedbacks(feedbacks);

			grouped.add(stepData);
		}
		
		for (UserStepData step : grouped) {
		    if (step.isCompleted() && step.getReports() != null && !step.getReports().isEmpty()) {
		        step.getReports().sort(Comparator.comparing(UserOsstRecord::getOsstRecordRegYmdt));
		        UserOsstRecord lastReport = step.getReports().get(step.getReports().size() - 1);
		        step.setFinalResults(List.of(lastReport));
		    } else {
		        step.setFinalResults(Collections.emptyList());
		    }
		}

		return grouped;
	}
	
	@Override
	public String generateNewOsrCode() {
	    String newCode = userOsstMapper.getNextOsrCode();
	    return newCode;
	}

	@Override
	public boolean insertRecord(UserOsstRecord record) {
	    return userOsstMapper.insertRecord(record) == 1;
	}
	
	@Override
	public boolean approveStep(String ospCd) {
		// 단계 승인
	    return userOsstMapper.updateStepApproval(ospCd) == 1;
	}
}