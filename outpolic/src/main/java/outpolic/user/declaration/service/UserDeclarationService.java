package outpolic.user.declaration.service;

import java.util.List;

import outpolic.user.declaration.domain.UserDeclaration;

public interface UserDeclarationService {
	
	// 신고 사유 드롭다운 조회
	List<UserDeclaration> getDeclarationReasonsByType(String dtCd);
	
	// 신고 타입 드롭다운 조회
	List<UserDeclaration> getActiveDeclarationTypes();
}
