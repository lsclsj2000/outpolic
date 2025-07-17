package outpolic.enter.points.mapper;

import org.apache.ibatis.annotations.Mapper;

import outpolic.enter.points.dto.EnterPointsDTO;

@Mapper
public interface EnterPointsMapper {
	EnterPointsDTO selectLatestPointsByMbrCd(String mbrCd);


    int insertPointsStatus(EnterPointsDTO pointsStatus);
}
