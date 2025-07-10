package outpolic.enter.ranking.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import outpolic.enter.ranking.domain.EnterPortfolioRankingContentsDTO;
import outpolic.enter.ranking.domain.EnterRankingContentsDTO;
import outpolic.enter.ranking.mapper.EnterRankingMapper;
import outpolic.enter.ranking.service.EnterRankingService;

@Service
public class EnterRankingServiceImpl implements EnterRankingService{

	@Autowired
	private EnterRankingMapper enterRankingMapper;

	public List<EnterRankingContentsDTO> getRankingContentsList() {

		return enterRankingMapper.getEnterRankingOsContents();
	}

	
	@Override
	public List<EnterPortfolioRankingContentsDTO> getEnterRankingPoContents() {
		return enterRankingMapper.getEnterRankingPoContents();
	}

}
