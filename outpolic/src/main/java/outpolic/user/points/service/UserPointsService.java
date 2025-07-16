package outpolic.user.points.service;

import outpolic.user.points.dto.UserPointsDTO;

public interface UserPointsService {
	
	UserPointsDTO getLatestPointsStatus(String mbrCd);

    
    boolean updatePoints(UserPointsDTO userPointsDTO);
}
