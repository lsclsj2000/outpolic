package outpolic.admin.loginHistory.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;


import outpolic.admin.loginHistory.dto.AdminLoginHistoryDTO;

@Mapper
public interface AdminLoginHistoryMapper {
	List<AdminLoginHistoryDTO> getAllHistory();
}
