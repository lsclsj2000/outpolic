package outpolic.admin.points.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import outpolic.admin.points.dto.AdminPointsHistoryDTO;
import outpolic.admin.points.dto.AdminPointsStandardDTO;

@Mapper
public interface AdminPointsMapper {
	
    // 마일리지 내역 조회
    List<AdminPointsHistoryDTO> selectPointsHistory(Map<String, Object> params);

    // 마일리지 기준 관련
    List<AdminPointsStandardDTO> selectAllStandards(Map<String, Object> params);
    AdminPointsStandardDTO selectStandardByCode(String epCd);
    int insertStandard(AdminPointsStandardDTO dto);
    int updateStandard(AdminPointsStandardDTO dto);
}
