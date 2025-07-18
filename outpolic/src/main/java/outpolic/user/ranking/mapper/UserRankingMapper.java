package outpolic.user.ranking.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import outpolic.user.ranking.domain.UserPortfolioRankingContentsDTO;
import outpolic.user.ranking.domain.UserRankingContentsDTO;

@Mapper
public interface UserRankingMapper {

	
	List<UserPortfolioRankingContentsDTO> getUserRankingPoContents(Map<String, Object> params);
	  
	List<UserRankingContentsDTO> getUserRankingOsContents(Map<String, Object> params);
	 
}
