package outpolic.user.ranking.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import outpolic.user.ranking.domain.UserRankingContentsDTO;
import outpolic.user.ranking.mapper.UserRankingMapper;
import outpolic.user.ranking.service.UserRankingService;

@Service
public class UserRankingServiceImpl implements UserRankingService{

	@Autowired
	private UserRankingMapper userRankingMapper;

	public List<UserRankingContentsDTO> getRankingContentsList() {

		return userRankingMapper.getUserRankingOsContents();
	}

}
