package outpolic.admin.osst.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import outpolic.admin.osst.domain.AdminOsst;

@Mapper
public interface AdminOsstMapper {
	
	// 외주 진행 수정 팝업창 업데이트
	void updateStepStatus(@Param("ocdCd") String ocdCd, @Param("stcCd") String stcCd, @Param("ospCustYn") Integer ospCustYn);
	
	// 외주 진행 목록 조회
	List<AdminOsst> getAdminOsstList();
	
	// 외주 진행 수정 팝업창 데이터 조회
	List<AdminOsst> getOsstStepsByOcdCd(String ocdCd);
}
