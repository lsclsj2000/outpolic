package outpolic.enter.osst.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import outpolic.enter.osst.domain.EnterOsst;
import outpolic.enter.osst.domain.EnterOsstRecord;


@Mapper
public interface EnterOsstMapper {
	
	// 진행 외주 기록 조회
	List<EnterOsstRecord> getEnterOsstRecord();
	
	// 진행 외주 상세 조회
	EnterOsst getEnterOsstDetail(String osstDetailCode);
	
	// 진행 외주 목록 조회
	List<EnterOsst> getEnterOsstList();
	
	// 진행 외주 단계 조회
	List<EnterOsst> getEnterOsstStcCode();
}