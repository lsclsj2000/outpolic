package outpolic.user.ranking.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import outpolic.user.ranking.domain.UserPortfolioRankingContentsDTO;
import outpolic.user.ranking.domain.UserRankingContentsDTO;

@Mapper
public interface UserRankingMapper {
	
	List<UserPortfolioRankingContentsDTO> getUserRankingPoContents();

	List<UserRankingContentsDTO> getUserRankingOsContents();
}
