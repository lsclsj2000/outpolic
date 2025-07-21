package outpolic.user.declaration.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import outpolic.user.declaration.domain.UserDeclaration;

public interface UserDeclarationService {
	
	// 신고 파일 업로드 
	void addDeclarationWithAttachments(UserDeclaration declaration, MultipartFile[] attachments);
	
	// 신고 대상 유형 검색 범위
	List<UserDeclaration> searchTarget(String type, String keyword);
	
	// 신고 사유 드롭다운 조회
	List<UserDeclaration> getDeclarationReasonsByType(String dtCd);
	
	// 신고 타입 드롭다운 조회
	List<UserDeclaration> getActiveDeclarationTypes();
}
