package outpolic.user.contents.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import outpolic.user.contents.domain.UserTodayView;

@Mapper
public interface UserTodayViewMapper {

	int existsInTodayView(@Param("clCd") String clCd);
    Integer selectMaxTdvCdNum();
    int insertNewTodayView(UserTodayView todayView); // 파라미터 타입 수정
    int incrementTodayView(@Param("clCd") String clCd);
}
