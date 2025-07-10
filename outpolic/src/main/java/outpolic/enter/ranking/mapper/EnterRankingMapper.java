package outpolic.enter.ranking.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import outpolic.enter.ranking.domain.EnterPortfolioRankingContentsDTO;
import outpolic.enter.ranking.domain.EnterRankingContentsDTO;

@Mapper
public interface EnterRankingMapper {
	
	List<EnterPortfolioRankingContentsDTO> getEnterRankingPoContents();

	List<EnterRankingContentsDTO> getEnterRankingOsContents();
}
