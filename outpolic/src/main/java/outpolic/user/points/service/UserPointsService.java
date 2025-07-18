package outpolic.user.points.service;

import outpolic.user.points.dto.UserPointsDTO;

public interface UserPointsService {
	
	UserPointsDTO getUserLatestPointsStatus(String mbrCd);

    
    boolean updateUserPoints(UserPointsDTO userPointsDTO);


	/**
	 * 특정 회원의 현재 마일리지 상태를 조회합니다.
	 * 가장 최근에 기록된 마일리지 상태를 반환합니다.
	 * @param mbrCd 회원 코드
	 * @return 회원의 최신 마일리지 상태 DTO (없으면 null)
	 */
	
}
