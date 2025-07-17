package outpolic.admin.statistics.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import outpolic.admin.statistics.domain.AdminViewStatDTO;
import outpolic.admin.statistics.mapper.AdminViewStatMapper;
import outpolic.admin.statistics.service.AdminViewStatService;

@Service
@RequiredArgsConstructor
public class AdminViewStatServiceImpl implements AdminViewStatService{

	private final AdminViewStatMapper adminViewStatMapper;
	
	@Override
	public List<AdminViewStatDTO> getPortfolioViewStats(String type, String startDate, String endDate) {
		if ("total".equals(type)) {
            return adminViewStatMapper.getPortfolioTotalViewStats();
        }
        return adminViewStatMapper.getPortfolioViewStatsByPeriod(startDate, endDate);
	}

	@Override
	public List<AdminViewStatDTO> getOutsourcingViewStats(String type, String startDate, String endDate) {
		if ("total".equals(type)) {
            return adminViewStatMapper.getOutsourcingTotalViewStats();
        }
        return adminViewStatMapper.getOutsourcingViewStatsByPeriod(startDate, endDate);
	}

}
