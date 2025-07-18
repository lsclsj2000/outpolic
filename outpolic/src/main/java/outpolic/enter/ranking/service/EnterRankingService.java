package outpolic.enter.ranking.service;

import java.util.List;

import outpolic.enter.ranking.domain.EnterPortfolioRankingContentsDTO;
import outpolic.enter.ranking.domain.EnterRankingContentsDTO;

public interface EnterRankingService {
	
	List<EnterPortfolioRankingContentsDTO> getEnterRankingPoContents(String userId);

	List<EnterRankingContentsDTO> getRankingContentsList(String userId);
}
