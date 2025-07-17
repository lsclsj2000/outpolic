package outpolic.admin.statistics.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import outpolic.admin.statistics.domain.AdminPortfolioRankingContentsDTO;
import outpolic.admin.statistics.domain.AdminRankingContentsDTO;
import outpolic.admin.statistics.domain.AdminSearchStatisticsDTO;
import outpolic.admin.statistics.service.AdminRankingService;
import outpolic.admin.statistics.service.AdminSearchStatisticsService;

@RestController 
@RequestMapping("/admin/api")
@RequiredArgsConstructor
public class AdminStatisticsApiController {

	 // 각 API에 필요한 서비스를 주입받습니다.
    private final AdminRankingService adminRankingService;
    private final AdminSearchStatisticsService searchStatisticsService;
    // private final AdminContentService adminContentService; // 비활성화 로직용 서비스

    @GetMapping("/search-statistics")
    public List<AdminSearchStatisticsDTO> getSearchStatistics(@RequestParam("targetDate") String targetDate) {
        return searchStatisticsService.getWeeklySearchStatistics(targetDate);
    }

    @GetMapping("/rankings/portfolio")
    public List<AdminPortfolioRankingContentsDTO> getPortfolioRankings(@RequestParam String targetDate) {
        return adminRankingService.getAdminRankingPoContents(targetDate);
    }

    @GetMapping("/rankings/outsourcing")
    public List<AdminRankingContentsDTO> getOutsourcingRankings(@RequestParam String targetDate) {
        return adminRankingService.getRankingContentsList(targetDate);
    }
    
    @PostMapping("/contents/{contentId}/deactivate")
    public String deactivateContent(@PathVariable String contentId) {
        // adminContentService.deactivate(contentId);
        return "SUCCESS";
    }
}
