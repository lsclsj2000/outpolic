package outpolic.admin.loginHistory.service.Impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import outpolic.admin.loginHistory.dto.AdminLoginHistoryDTO;
import outpolic.admin.loginHistory.mapper.AdminLoginHistoryMapper;
import outpolic.admin.loginHistory.service.AdminLoginHistoryService;

@Service
@RequiredArgsConstructor
public class AdminLoginHistoryServiceImpl implements AdminLoginHistoryService {
	
	private final AdminLoginHistoryMapper adminLoginHistoryMapper;
	// 로그인 기록을 가져옴
	@Override
	public List<AdminLoginHistoryDTO> getAllHistory(int startRow, int rowPerPage) {
	    return adminLoginHistoryMapper.getAllHistory(startRow, rowPerPage);
	}
	@Override
	public int getAllHistoryCount() {
		return adminLoginHistoryMapper.getAllHistoryCount();
	}
	
	// 최근 7일의 로그인기록 조회
	@Override
	public List<Map<String, Object>> getLoginCountForLast7Days() {
		return adminLoginHistoryMapper.getLoginCountForLast7Days();
	}

	// 기간 검색 기록
	@Override
	public List<AdminLoginHistoryDTO> searchLoginHistory(String keyword, String startDate, String endDate) {
	    return adminLoginHistoryMapper.searchLoginHistory(keyword, startDate, endDate);
	}


	


}
