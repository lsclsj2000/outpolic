package outpolic.admin.loginHistory.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import outpolic.admin.loginHistory.dto.AdminLoginHistoryDTO;

public interface AdminLoginHistoryService {
	List<AdminLoginHistoryDTO> getAllHistory(int startRow, int rowPerPage);
	
	 int getAllHistoryCount();
	
	List<Map<String, Object>> getLoginCountForLast7Days();
	
	List<AdminLoginHistoryDTO> searchLoginHistory(String keyword, String startDate, String endDate);

	void updateLogoutTimeBySession(String memberCode);
}
