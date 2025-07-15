package outpolic.admin.statistics.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import outpolic.admin.statistics.domain.AdminPortfolioRankingContentsDTO;
import outpolic.admin.statistics.domain.AdminRankingContentsDTO;
import outpolic.admin.statistics.mapper.AdminRankingMapper;
import outpolic.admin.statistics.service.AdminRankingService;

@Service
public class AdminRankingServiceImpl implements AdminRankingService{

	@Autowired
	private AdminRankingMapper adminRankingMapper;
	
	@Override
	public List<AdminPortfolioRankingContentsDTO> getAdminRankingPoContents(String targetDate) {
		
		return adminRankingMapper.getAdminRankingPoContents(targetDate);
	}

	@Override
	public List<AdminRankingContentsDTO> getRankingContentsList(String targetDate) {

		return adminRankingMapper.getAdminRankingOsContents(targetDate);
	}

}
