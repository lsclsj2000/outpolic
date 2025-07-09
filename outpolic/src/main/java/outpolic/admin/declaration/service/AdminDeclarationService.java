package outpolic.admin.declaration.service;

import java.util.List;

import outpolic.admin.declaration.domain.AdminDeclaration;

public interface AdminDeclarationService {
	
	// 신고 타입 자원 조회
	List<AdminDeclaration> getAdminDeclarationTypeList();
	
	// 신고 내역 목록 조회
	List<AdminDeclaration> getAdminDeclarationList();
}
