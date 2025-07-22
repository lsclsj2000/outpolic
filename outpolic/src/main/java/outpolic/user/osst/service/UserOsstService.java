package outpolic.user.osst.service;

import java.util.List;

import outpolic.user.osst.domain.UserOsst;
import outpolic.user.osst.domain.UserOsstRecord;
import outpolic.user.osst.domain.UserStepData;

public interface UserOsstService {
	// 단계 승인
	boolean approveStep(String ospCd);
		
	// osr_cd 자동생성
	String generateNewOsrCode();
	
	// 보고 피드백 제출
	boolean insertRecord(UserOsstRecord record);
	
	// 진행 외주 기록 조회
	List<UserOsstRecord> getOutsourcingRecordsByOcdCd(String ocdCd);
	
	// 진행 외주 상세 조회
	UserOsst getUserOsstDetail(String osstDetailCode, String memberCode);
	
	// 진행 외주 목록 조회
	List<UserOsst> getUserOsstList(String memberCode);
	
	// 진행 외주 단계
	List<UserOsst> getUserOsstStcCode(String ocdCd);
	
	// 조건별 진행 외주 단계 
	List<UserStepData> getGroupedStepData(String ocdCd);
}
