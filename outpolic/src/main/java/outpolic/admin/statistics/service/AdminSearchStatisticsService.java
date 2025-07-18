package outpolic.admin.statistics.service;

import java.util.List;

import outpolic.admin.statistics.domain.AdminSearchStatisticsDTO;

public interface AdminSearchStatisticsService {

	void aggregateWeeklySearchTerms();
	
	List<AdminSearchStatisticsDTO> getWeeklySearchStatistics(String startDate, String endDate, String type, int minCount);
	
}
