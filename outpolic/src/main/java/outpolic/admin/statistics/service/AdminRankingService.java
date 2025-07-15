package outpolic.admin.statistics.service;

import java.util.List;

import org.springframework.stereotype.Service;
import outpolic.admin.statistics.domain.AdminPortfolioRankingContentsDTO;
import outpolic.admin.statistics.domain.AdminRankingContentsDTO;

public interface AdminRankingService {
	
	List<AdminPortfolioRankingContentsDTO> getAdminRankingPoContents();

	List<AdminRankingContentsDTO> getRankingContentsList();

}
