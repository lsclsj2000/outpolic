package outpolic.admin.statistics.service;

import java.util.List;

import outpolic.admin.statistics.domain.AdminViewStatDTO;

public interface AdminViewStatService {

	List<AdminViewStatDTO> getPortfolioViewStats(String type, String startDate, String endDate);
    List<AdminViewStatDTO> getOutsourcingViewStats(String type, String startDate, String endDate);
}
