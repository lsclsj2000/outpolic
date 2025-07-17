package outpolic.enter.points.service.impl;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import outpolic.enter.points.dto.EnterPointsDTO;
import outpolic.enter.points.mapper.EnterPointsMapper;
import outpolic.enter.points.service.EnterPointsService;

@Service
public class EnterPointsServiceImpl implements EnterPointsService {
	@Autowired
    private EnterPointsMapper enterPointsMapper;

    /**
     * 특정 회원의 현재 마일리지 상태를 조회합니다.
     * 가장 최근에 기록된 마일리지 상태를 반환합니다.
     * @param mbrCd 회원 코드
     * @return 회원의 최신 마일리지 상태 DTO (없으면 null)
     */
	
    @Override
    public EnterPointsDTO getEnterLatestPointsStatus(String mbrCd) {
        return enterPointsMapper.selectLatestPointsByMbrCd(mbrCd);
    }

    /**
     * 회원의 마일리지를 적립하거나 사용합니다.
     * 새로운 마일리지 내역을 추가하고, 필요시 현재 마일리지(누적 마일리지)를 업데이트합니다.
     * @param userPointsDTO 마일리지 변동 정보 (ptsPointsDelta, ptsStatus 등 포함)
     * @return 성공 여부 (true: 성공, false: 실패)
     */
    @Override
    @Transactional // 트랜잭션 관리
    public boolean updateEnterPoints(EnterPointsDTO enterPointsDTO) {
        // 1. 현재 회원의 최신 마일리지 상태를 조회합니다.
        // 수정: userPointsDTO에서 mbrCd를 가져와야 합니다.
    	EnterPointsDTO currentPoints = enterPointsMapper.selectLatestPointsByMbrCd(enterPointsDTO.getMbrCd());

        BigDecimal currentCumPoints = (currentPoints != null) ? currentPoints.getPtsCumPoints() : BigDecimal.ZERO;
        BigDecimal ptsDelta = enterPointsDTO.getPtsPointsDelta();

        // 2. 새로운 누적 마일리지를 계산합니다.
        BigDecimal newCumPoints;
        if ("적립".equals(enterPointsDTO.getPtsStatus())) {
            newCumPoints = currentCumPoints.add(ptsDelta);
        } else if ("사용".equals(enterPointsDTO.getPtsStatus())) {
            newCumPoints = currentCumPoints.subtract(ptsDelta);
            // 마일리지가 음수가 되지 않도록 방지 (프론트엔드에서 1차 검증하지만 백엔드에서도 방어 로직)
            if (newCumPoints.compareTo(BigDecimal.ZERO) < 0) {
                newCumPoints = BigDecimal.ZERO; // 마이너스 방지
                // 또는 예외를 발생시켜 마일리지 부족 알림
                // throw new IllegalArgumentException("마일리지가 부족합니다.");
            }
        } else {
            // 알 수 없는 상태 코드
            return false;
        }

        // 3. PointsStatus DTO에 계산된 누적 마일리지를 설정합니다.
        enterPointsDTO.setPtsCumPoints(newCumPoints);
        // 현재 마일리지는 누적 마일리지와 동일하게 설정합니다.
        enterPointsDTO.setPtsPoints(newCumPoints);
        
        // 4. 새로운 마일리지 내역을 DB에 삽입합니다.
        int result = enterPointsMapper.insertPointsStatus(enterPointsDTO);

        return result > 0;
    }
}
