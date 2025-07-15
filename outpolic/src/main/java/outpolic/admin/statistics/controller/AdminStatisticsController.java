package outpolic.admin.statistics.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.admin.statistics.domain.AdminPortfolioRankingContentsDTO;
import outpolic.admin.statistics.domain.AdminRankingContentsDTO;
import outpolic.admin.statistics.service.AdminRankingService;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminStatisticsController {
	
	private final AdminRankingService adminRankingService;

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
	
	 // 2. 포트폴리오 랭킹 데이터 API
    @GetMapping("/api/admin/rankings/portfolio")
    @ResponseBody
    public List<AdminPortfolioRankingContentsDTO> getPortfolioRankings() {
        return adminRankingService.getAdminRankingPoContents();
    }

    // 3. 외주 랭킹 데이터 API
    @GetMapping("/api/admin/rankings/outsourcing")
    @ResponseBody
    public List<AdminRankingContentsDTO> getOutsourcingRankings() {
        return adminRankingService.getRankingContentsList();
    }
	

}
