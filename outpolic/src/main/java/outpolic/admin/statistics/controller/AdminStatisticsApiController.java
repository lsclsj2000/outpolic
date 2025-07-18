package outpolic.admin.statistics.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
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
import outpolic.admin.statistics.domain.AdminViewStatDTO;
import outpolic.admin.statistics.service.AdminRankingService;
import outpolic.admin.statistics.service.AdminSearchStatisticsService;
import outpolic.admin.statistics.service.AdminViewStatService;

@RestController 
@RequestMapping("/admin/api")
@RequiredArgsConstructor
public class AdminStatisticsApiController {

	 // 각 API에 필요한 서비스를 주입받습니다.
    private final AdminRankingService adminRankingService;
    private final AdminSearchStatisticsService searchStatisticsService;
    private final AdminViewStatService adminViewStatService;
    // private final AdminContentService adminContentService; // 비활성화 로직용 서비스
    
    @GetMapping("/views/outsourcing")
    public List<AdminViewStatDTO> getOutsourcingViewStats(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        return adminViewStatService.getOutsourcingViewStats(type, startDate, endDate);
    }
    
    @GetMapping("/views/portfolio")
    public List<AdminViewStatDTO> getPortfolioViewStats(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        return adminViewStatService.getPortfolioViewStats(type, startDate, endDate);
    }

    @GetMapping("/search-statistics")
    // ★★ 반환 타입을 ResponseEntity<List<...>> 로 수정합니다. ★★
    public ResponseEntity<List<AdminSearchStatisticsDTO>> getSearchStatistics(
                                          @RequestParam(value = "startDate", required = false) String startDate,
                                          @RequestParam(value = "endDate", required = false) String endDate,
                                          @RequestParam(value = "type", required = false) String type,
                                          @RequestParam(value = "minCount", defaultValue = "1") int minCount) {
    
        // 서비스 메소드 이름은 getWeeklySearchStatistics 이지만, 내부 로직은 기간별 조회를 수행합니다.
        List<AdminSearchStatisticsDTO> stats = searchStatisticsService.getWeeklySearchStatistics(startDate, endDate, type, minCount);
    
        // 이제 타입이 일치하여 정상적으로 동작합니다.
        return ResponseEntity.ok(stats);
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
