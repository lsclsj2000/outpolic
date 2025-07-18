package outpolic.admin.declaration.service;

import java.util.List;

import outpolic.admin.declaration.domain.AdminDeclaration;

public interface AdminDeclarationService {
	
	// 신고 타입 등록
	void insertDeclarationType(AdminDeclaration declaration);
	
	// 신고 사유 등록
    void insertDeclarationReason(AdminDeclaration declaration);
    
    // 신고처리결과 등록
    void insertDeclarationResult(AdminDeclaration declaration);
	
	// 신고 처리 결과 자원 조회
	List<AdminDeclaration> getAdminDeclarationResultList();
	
	// 신고 사유 자원 조회
	List<AdminDeclaration> getAdminDeclarationReasonList();
	
	// 신고 타입 자원 조회
	List<AdminDeclaration> getAdminDeclarationTypeList();
	
	// 신고 내역 목록 조회
	List<AdminDeclaration> getAdminDeclarationList();
}
