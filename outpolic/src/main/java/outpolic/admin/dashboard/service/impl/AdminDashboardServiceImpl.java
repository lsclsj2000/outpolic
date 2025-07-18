package outpolic.admin.dashboard.service.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import outpolic.admin.dashboard.domain.AdminDashboardDTO;
import outpolic.admin.dashboard.mapper.AdminDashboardMapper;
import outpolic.admin.dashboard.service.AdminDashboardService;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService{

	private final AdminDashboardMapper dashboardMapper;
	
	@Override
	public AdminDashboardDTO getDashboardSummary() {
		AdminDashboardDTO dto = new AdminDashboardDTO();
        
        dto.setTotalMemberCount(dashboardMapper.getTotalMemberCount());
        dto.setTodayNewMemberCount(dashboardMapper.getTodayNewMemberCount());
        dto.setTotalPortfolioCount(dashboardMapper.getTotalPortfolioCount());
        dto.setTotalOutsourcingCount(dashboardMapper.getTotalOutsourcingCount());
        dto.setTotalRevenue(dashboardMapper.getTotalRevenue());
        dto.setMonthlyRevenue(dashboardMapper.getMonthlyRevenue());
        dto.setWeeklyNewMembers(dashboardMapper.getWeeklyNewMembers());
        
        return dto;
	}

}
