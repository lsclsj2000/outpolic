package outpolic.admin.statistics.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import outpolic.admin.statistics.domain.AdminViewStatDTO;

@Mapper
public interface AdminViewStatMapper {

	List<AdminViewStatDTO> getPortfolioViewStatsByPeriod(@Param("startDate") String startDate, @Param("endDate") String endDate);
    List<AdminViewStatDTO> getOutsourcingViewStatsByPeriod(@Param("startDate") String startDate, @Param("endDate") String endDate);

    // 전체 누적 조회
    List<AdminViewStatDTO> getPortfolioTotalViewStats();
    List<AdminViewStatDTO> getOutsourcingTotalViewStats();
}
