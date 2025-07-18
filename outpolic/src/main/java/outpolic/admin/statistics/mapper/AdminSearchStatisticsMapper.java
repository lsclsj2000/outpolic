package outpolic.admin.statistics.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import outpolic.admin.statistics.domain.AdminSearchStatisticsDTO;


@Mapper
public interface AdminSearchStatisticsMapper {

	void insertWeeklySearchStatistics(@Param("stCd") String stCd, 
            @Param("frYmd") String frYmd, 
            @Param("toYmd") String toYmd);


	/**
	* 특정 날짜(targetDate)가 포함된 주간의 검색어 통계를 조회합니다.
	* XML의 id="findWeeklySearchStatisticsByDate"와 연결됩니다.
	* @param targetDate 조회할 날짜 (yyyy-MM-dd)
	* @return 검색어 통계 DTO 리스트
	*/
	List<AdminSearchStatisticsDTO> findStatisticsByPeriod(Map<String, Object> params);

	/**
	* search_terms 테이블의 다음 st_cd를 생성하기 위한 메소드입니다.
	* XML의 id="getNextStCd"와 연결됩니다.
	* @return 생성할 다음 st_cd (예: SST00001)
	*/
	String getNextStCd();
}
