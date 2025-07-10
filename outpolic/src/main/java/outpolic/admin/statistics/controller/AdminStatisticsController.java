package outpolic.admin.statistics.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminStatisticsController {

	@GetMapping("/contentsViews")
	public String contentsViewsStatisticsView() {
		return "admin/statistics/adminContentsViewsStatisticsView";
	}
	
	@GetMapping("/RankingStatistics")
	public String RankingStatisticsView() {
		return "admin/statistics/adminRankingStatisticsView";
	}
	
	@GetMapping("/SearchStatistics")
	public String SearchStatisticsView() {
		return "admin/statistics/adminSearchStatisticsView";
	}
	

}
