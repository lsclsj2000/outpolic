package outpolic.user.ranking.service;

import java.util.List;

import outpolic.user.ranking.domain.UserRankingContentsDTO;

public interface UserRankingService {

	List<UserRankingContentsDTO> getRankingContentsList();
}
