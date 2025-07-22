package outpolic.enter.osst.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import outpolic.enter.osst.domain.EnterOsst;
import outpolic.enter.osst.domain.EnterOsstRecord;


@Mapper
public interface EnterOsstMapper {
	
	// 단계 승인
	int updateStepApproval(@Param("ospCd") String ospCd);
	
	// osr_cd 자동생성
	String getNextOsrCode();
	
	// 보고 피드백 제출
	int insertRecord(EnterOsstRecord record);
	
	// 진행 외주 상세 조회
	EnterOsst getEnterOsstDetail(@Param("osstDetailCode") String osstDetailCode, @Param("memberCode") String memberCode);
	
	// 진행 외주 목록 조회
	List<EnterOsst> getEnterOsstList(String memberCode);
	
	// 진행 외주 단계 조회
	List<EnterOsst> getEnterOsstStcCode(@Param("ocdCd") String ocdCd);
	
	// 외주 기록 조회 (ocdCd 기준으로 조회하는 메서드 이름 변경)
	List<EnterOsstRecord> getOutsourcingRecordsByOcdCd(@Param("ocdCd") String ocdCd);
}