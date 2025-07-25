package outpolic.enter.osst.service;
	
import java.util.List;

import outpolic.enter.osst.domain.EnterOsst;
import outpolic.enter.osst.domain.EnterOsstRecord;
import outpolic.enter.osst.domain.EnterStepData;

public interface EnterOsstService {
	
	// 단계 승인
	boolean approveStep(String ospCd);
	
	// osr_cd 자동생성
	String generateNewOsrCode();
	
	// 보고 피드백 제출
	boolean insertRecord(EnterOsstRecord record);
	
	// 진행 외주 기록 조회
	List<EnterOsstRecord> getOutsourcingRecordsByOcdCd(String ocdCd);
	
	// 진행 외주 상세 조회
	EnterOsst getEnterOsstDetail(String osstDetailCode, String memberCode);
	
	// 진행 외주 목록 조회
	List<EnterOsst> getEnterOsstList(String memberCode);
	
	// 진행 외주 단계
	List<EnterOsst> getEnterOsstStcCode(String ocdCd);
	
	// 조건별 진행 외주 단계 
	List<EnterStepData> getGroupedStepData(String ocdCd);
}
