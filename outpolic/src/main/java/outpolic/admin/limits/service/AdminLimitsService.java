package outpolic.admin.limits.service;

import java.util.List;

import outpolic.admin.limits.domain.AdminLimits;

public interface AdminLimitsService {
	
	// 제재 내역 조회
	List<AdminLimits> getAdminLimitsList();
	
	// 제재 타입 자원 조회
	List<AdminLimits> getAdminLimitsTypeList();
}
