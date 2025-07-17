package outpolic.admin.statistics.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.admin.statistics.domain.AdminPortfolioRankingContentsDTO;
import outpolic.admin.statistics.domain.AdminRankingContentsDTO;
import outpolic.admin.statistics.domain.AdminSearchStatisticsDTO;
import outpolic.admin.statistics.service.AdminRankingService;
import outpolic.admin.statistics.service.AdminSearchStatisticsService;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminStatisticsController {
	
	@GetMapping("/contentsViews")
	public String contentsViewsStatisticsView() {
		return "admin/statistics/adminContentsViewsStatisticsView";
	}
	
	@GetMapping("/SearchStatistics")
	public String SearchStatisticsView() {
		return "admin/statistics/adminSearchStatisticsView";
	}
	
	@GetMapping("/RankingStatistics")
	public String RankingStatisticsView() {
		return "admin/statistics/adminRankingStatisticsView";
	}

}
