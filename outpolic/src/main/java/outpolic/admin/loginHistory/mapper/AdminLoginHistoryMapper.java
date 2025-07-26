package outpolic.admin.loginHistory.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import outpolic.admin.loginHistory.dto.AdminLoginHistoryDTO;

@Mapper
public interface AdminLoginHistoryMapper {
	List<AdminLoginHistoryDTO> getAllHistory(    
			@Param("startRow") int startRow,
		    @Param("rowPerPage") int rowPerPage);
	
	List<Map<String, Object>> getLoginCountForLast7Days();
	
	List<AdminLoginHistoryDTO> searchLoginHistory(
		    @Param("keyword") String keyword,
		    @Param("startDate") String startDate,
		    @Param("endDate") String endDate);
	
	int countSearchLoginHistory(
		    @Param("keyword") String keyword,
		    @Param("startDate") String startDate,
		    @Param("endDate") String endDate
		);

	int getAllHistoryCount();
	
	void updateLogoutTimeByMemberCode(String memberCode);
		    
}
