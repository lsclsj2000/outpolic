package outpolic.user.ranking.service;

import java.util.List;

import outpolic.user.ranking.domain.UserPortfolioRankingContentsDTO;
import outpolic.user.ranking.domain.UserRankingContentsDTO;

public interface UserRankingService {


	List<UserPortfolioRankingContentsDTO> getUserRankingPoContents(String userId);
	List<UserRankingContentsDTO> getRankingContentsList(String userId);

}
