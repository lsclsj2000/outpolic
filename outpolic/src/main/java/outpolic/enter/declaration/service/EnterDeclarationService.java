package outpolic.enter.declaration.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import outpolic.enter.declaration.domain.EnterDeclaration;

public interface EnterDeclarationService {
	
	// 신고 파일 업로드 
	void addDeclarationWithAttachments(EnterDeclaration declaration, MultipartFile[] attachments);
	
	// 신고 대상 유형 검색 범위
	List<EnterDeclaration> searchTarget(String type, String keyword);
	
	// 신고 사유 드롭다운 조회
	List<EnterDeclaration> getDeclarationReasonsByType(String dtCd);
	
	// 신고 타입 드롭다운 조회
	List<EnterDeclaration> getActiveDeclarationTypes();
}
