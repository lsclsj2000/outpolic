package outpolic.admin.statistics.service;

import java.util.List;

import org.springframework.web.bind.annotation.RequestParam;

import outpolic.admin.statistics.domain.AdminPortfolioRankingContentsDTO;
import outpolic.admin.statistics.domain.AdminRankingContentsDTO;

public interface AdminRankingService {
	
	List<AdminPortfolioRankingContentsDTO> getAdminRankingPoContents(String targetDate);

	List<AdminRankingContentsDTO> getRankingContentsList(String targetDate);

}
