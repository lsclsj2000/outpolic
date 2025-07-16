package outpolic.user.points.mapper;

import org.apache.ibatis.annotations.Mapper;

import outpolic.user.points.dto.UserPointsDTO;

@Mapper
public interface UserPointsMapper {

    UserPointsDTO selectLatestPointsByMbrCd(String mbrCd);


    int insertPointsStatus(UserPointsDTO pointsStatus);
}
