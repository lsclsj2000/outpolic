package outpolic.enter.points.service;

import outpolic.enter.points.dto.EnterPointsDTO;

public interface EnterPointsService {

	EnterPointsDTO getEnterLatestPointsStatus(String mbrCd);

    
    boolean updateEnterPoints(EnterPointsDTO enterPointsDTO);
}
