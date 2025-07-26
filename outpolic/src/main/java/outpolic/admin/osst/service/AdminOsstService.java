package outpolic.admin.osst.service;

import java.util.List;

import org.springframework.data.repository.query.Param;

import outpolic.admin.osst.domain.AdminOsst;

public interface AdminOsstService {
	
	// 외주 진행 수정 팝업창 업데이트
	void updateStepStatus(String ocdCd, String stcCd, Integer ospCustYn);
	
	// 외주 진행 수정 팝업창 데이터 조회
	List<AdminOsst> getOsstStepsByOcdCd(String ocdCd);
	
	// 외주 진행 목록 조회
	List<AdminOsst> getAdminOsstList();
	
	// 외주 진행 필터
	List<AdminOsst> getAdminOsstListFiltered(@Param("field") String field,
            @Param("keyword") String keyword,
            @Param("stepStatus") String stepStatus);
}
