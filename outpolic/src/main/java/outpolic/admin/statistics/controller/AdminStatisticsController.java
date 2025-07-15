package outpolic.admin.statistics.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    @GetMapping("/api/rankings/portfolio")
    @ResponseBody
    public List<AdminPortfolioRankingContentsDTO> getPortfolioRankings(@RequestParam String targetDate) {
        return adminRankingService.getAdminRankingPoContents(targetDate);
    }

    // 3. 외주 랭킹 데이터 API
    @GetMapping("/api/rankings/outsourcing")
    @ResponseBody
    public List<AdminRankingContentsDTO> getOutsourcingRankings(@RequestParam String targetDate) {
        return adminRankingService.getRankingContentsList(targetDate);
    }
	
    @PostMapping("/api/contents/{contentId}/deactivate")
    @ResponseBody
    public String deactivateContent(@PathVariable String contentId) {
        // 여기에 contentId를 받아 해당 콘텐츠의 stc_code를 'STC02'(비활성)로
        // 업데이트하는 Service 로직을 호출하는 코드가 필요합니다.
        // adminContentService.deactivate(contentId); 와 같은 형태가 될 것입니다.
        return "SUCCESS";
    }

}
