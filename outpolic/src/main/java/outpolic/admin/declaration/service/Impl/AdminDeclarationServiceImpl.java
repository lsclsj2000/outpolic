package outpolic.admin.declaration.service.Impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.admin.declaration.domain.AdminDeclaration;
import outpolic.admin.declaration.mapper.AdminDeclarationMapper;
import outpolic.admin.declaration.service.AdminDeclarationService;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AdminDeclarationServiceImpl implements AdminDeclarationService {
	
	private final AdminDeclarationMapper adminDeclarationMapper;

	@Override
	public List<AdminDeclaration> getAdminDeclarationList() {
		// 신고 내역 목록 조회
		List<AdminDeclaration> adminDeclarationList = adminDeclarationMapper.getAdminDeclarationList();
		return adminDeclarationList;
	}
}
