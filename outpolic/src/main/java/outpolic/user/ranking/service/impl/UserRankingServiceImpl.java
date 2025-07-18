package outpolic.user.ranking.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import outpolic.user.ranking.domain.UserPortfolioRankingContentsDTO;
import outpolic.user.ranking.domain.UserRankingContentsDTO;
import outpolic.user.ranking.mapper.UserRankingMapper;
import outpolic.user.ranking.service.UserRankingService;

@Service
public class UserRankingServiceImpl implements UserRankingService{


	 @Autowired 
	 private UserRankingMapper userRankingMapper;
	  
	 public List<UserRankingContentsDTO> getRankingContentsList(String userId) {
		 Map<String, Object> params = new HashMap<>();
	     params.put("userId", userId);
	 
		 return userRankingMapper.getUserRankingOsContents(params); 
	 }
	 
	 
	 
	 @Override 
	 public List<UserPortfolioRankingContentsDTO> getUserRankingPoContents(String userId) { 
		 Map<String, Object> params = new HashMap<>();
	     params.put("userId", userId);
		 
		 return userRankingMapper.getUserRankingPoContents(params); 
	 }

}
