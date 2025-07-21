package outpolic.admin.dashboard.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import outpolic.admin.dashboard.domain.AdminDashboardDTO.DailyCount;

@Mapper
public interface AdminDashboardMapper {

	long getTotalMemberCount();
    long getTodayNewMemberCount();
    long getTotalPortfolioCount();
    long getTotalOutsourcingCount();
    Long getTotalRevenue(); // SUM 결과는 null일 수 있으므로 Long으로 받습니다.
    Long getMonthlyRevenue();
    List<DailyCount> getWeeklyNewMembers();
}
