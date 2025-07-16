package outpolic.enter.contents.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import outpolic.enter.contents.domain.EnterTodayView;

@Mapper
public interface EnterTodayViewMapper {

	int existsInTodayView(@Param("clCd") String clCd);
    Integer selectMaxTdvCdNum();
    int insertNewTodayView(EnterTodayView todayView); // 파라미터 타입 수정
    int incrementTodayView(@Param("clCd") String clCd);
}
