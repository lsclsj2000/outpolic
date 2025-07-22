package outpolic.admin.points.service;

import java.util.List;
import java.util.Map;

import outpolic.admin.points.dto.AdminPointsHistoryDTO;
import outpolic.admin.points.dto.AdminPointsStandardDTO;

public interface AdminPointsService {
	 List<AdminPointsHistoryDTO> getEarnHistory(Map<String, Object> params);
	 
    // 마일리지 기준 관련 메소드 추가
    List<AdminPointsStandardDTO> getStandardList(Map<String, Object> params);
    AdminPointsStandardDTO getStandardByCode(String epCd);
    void registerStandard(AdminPointsStandardDTO dto);
    void updateStandard(AdminPointsStandardDTO dto);
}
