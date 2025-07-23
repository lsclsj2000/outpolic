package outpolic.admin.points.service.impl;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import outpolic.admin.points.dto.AdminPointsHistoryDTO;
import outpolic.admin.points.dto.AdminPointsStandardDTO;
import outpolic.admin.points.mapper.AdminPointsMapper;
import outpolic.admin.points.service.AdminPointsService;

@Service
@RequiredArgsConstructor
public class AdminPointsServiceImpl implements AdminPointsService {
	
	private final AdminPointsMapper adminPointsMapper;
	
	@Override
    public List<AdminPointsHistoryDTO> getEarnHistory(Map<String, Object> params) {
        // '적립' 내역만 조회하도록 파라미터를 추가
        params.put("ptsStatus", "적립");
        return adminPointsMapper.selectPointsHistory(params);
    }

	// 마일리지 기준 관련 메소드 구현
    @Override
    public List<AdminPointsStandardDTO> getStandardList(Map<String, Object> params) {
        return adminPointsMapper.selectAllStandards(params);
    }

    @Override
    public AdminPointsStandardDTO getStandardByCode(String epCd) {
        return adminPointsMapper.selectStandardByCode(epCd);
    }

    @Override
    public void registerStandard(AdminPointsStandardDTO dto) {
        dto.setEpRegYmdt(new Timestamp(System.currentTimeMillis()));
        // epStatus는 DB 쿼리에서 기본값 1(활성)으로 들어갑니다.
        adminPointsMapper.insertStandard(dto);
    }

    @Override
    public void updateStandard(AdminPointsStandardDTO dto) {
        // [추가] 수정 시간을 서비스에서 설정
        dto.setEpMdfcnYmdt(new Timestamp(System.currentTimeMillis()));
        adminPointsMapper.updateStandard(dto);
    }

}
