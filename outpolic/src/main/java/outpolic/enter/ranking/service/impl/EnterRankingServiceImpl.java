package outpolic.enter.ranking.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	public List<EnterRankingContentsDTO> getRankingContentsList(String userId) {
		Map<String, Object> params = new HashMap<>();
	    params.put("userId", userId);

		return enterRankingMapper.getEnterRankingOsContents(params);
	}

	
	@Override
	public List<EnterPortfolioRankingContentsDTO> getEnterRankingPoContents(String userId) {
		Map<String, Object> params = new HashMap<>();
	    params.put("userId", userId);
		return enterRankingMapper.getEnterRankingPoContents(params);
	}



}
