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

	@Override
	public List<AdminDeclaration> getAdminDeclarationTypeList() {
		// 신고 타입 자원 조회
		List<AdminDeclaration> adminDeclarationList= adminDeclarationMapper.getAdminDeclarationTypeList();
		
		return adminDeclarationList;
	}

	@Override
	public List<AdminDeclaration> getAdminDeclarationReasonList() {
		// 신고 사유 자원 조회
		List<AdminDeclaration> adminDeclarationReasonList  = adminDeclarationMapper.getAdminDeclarationReasonList();
		return adminDeclarationReasonList;
	}

	@Override
	public List<AdminDeclaration> getAdminDeclarationResultList() {
		// 신고 처리 결과 자원 조회
		List<AdminDeclaration> adminDeclarationResultList  = adminDeclarationMapper.getAdminDeclarationResultList();
		return adminDeclarationResultList;
	}
	
	@Override
    public void insertDeclarationType(AdminDeclaration declaration) {
		// 신고 타입 등록
		adminDeclarationMapper.insertDeclarationType(declaration);
    }

    @Override
    public void insertDeclarationReason(AdminDeclaration declaration) {
    	// 신고 사유 등록
    	adminDeclarationMapper.insertDeclarationReason(declaration);
    }

    @Override
    public void insertDeclarationResult(AdminDeclaration declaration) {
    	// 신고처리결과 등록
    	adminDeclarationMapper.insertDeclarationResult(declaration);
    }
	
}
