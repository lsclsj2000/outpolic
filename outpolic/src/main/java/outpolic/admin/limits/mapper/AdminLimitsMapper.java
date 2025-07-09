package outpolic.admin.limits.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import outpolic.admin.limits.domain.AdminLimits;

@Mapper
public interface AdminLimitsMapper {
	
	// 제재 내역 조회
	List<AdminLimits> getAdminLimitsList();
	
	// 제재 타입 자원 조회
	List<AdminLimits> getAdminLimitsTypeList();
}
