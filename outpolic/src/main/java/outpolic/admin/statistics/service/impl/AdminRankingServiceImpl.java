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
	public List<AdminPortfolioRankingContentsDTO> getAdminRankingPoContents() {
		
		return adminRankingMapper.getAdminRankingPoContents();
	}

	@Override
	public List<AdminRankingContentsDTO> getRankingContentsList() {

		return adminRankingMapper.getAdminRankingOsContents();
	}

}
