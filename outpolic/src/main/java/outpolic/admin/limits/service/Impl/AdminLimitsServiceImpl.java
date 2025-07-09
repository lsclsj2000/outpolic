package outpolic.admin.limits.service.Impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.admin.limits.domain.AdminLimits;
import outpolic.admin.limits.mapper.AdminLimitsMapper;
import outpolic.admin.limits.service.AdminLimitsService;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AdminLimitsServiceImpl implements AdminLimitsService {
	
	private final AdminLimitsMapper adminLimitsMapper;

	@Override
	public List<AdminLimits> getAdminLimitsTypeList() {
		// 제재 타입 자원 조회
		List<AdminLimits> adminLimitsTypeList = adminLimitsMapper.getAdminLimitsTypeList();
		return adminLimitsTypeList;
	}

	@Override
	public List<AdminLimits> getAdminLimitsList() {
		// 제재 내역 조회
		List<AdminLimits> adminLimitsList = adminLimitsMapper.getAdminLimitsList();
		
		return adminLimitsList;
	}

	@Override
	public List<AdminLimits> getAdminLimitsAuthorityList() {
		// 회원 권한 조회
		List<AdminLimits> adminLimitsAuthorityList = adminLimitsMapper.getAdminLimitsAuthorityList();
		
		return adminLimitsAuthorityList;
	}

}
