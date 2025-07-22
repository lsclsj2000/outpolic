package outpolic.enter.declaration.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import outpolic.enter.declaration.domain.EnterDeclaration;
import outpolic.user.inquiry.domain.UserInquiryFile;

@Mapper
public interface EnterDeclarationMapper {
	
	// 첨부파일 코드 생성
	String generateNewSaCode(); 
	
	// 신고 등록
	int insertDeclaration(EnterDeclaration declaration);

	// 신고 파일 업로드
	int insertDeclarationAttachments(List<UserInquiryFile> attachments);
	
	// 신고 대상 유형 검색 범위-포트폴리오
	List<EnterDeclaration> searchPortfolio(@Param("keyword") String keyword);
	
	// 신고 대상 유형 검색 범위-외주
	List<EnterDeclaration> searchOutsourcing(@Param("keyword") String keyword);
	
	// 신고 대상 유형 검색 범위-기업회원
	List<EnterDeclaration> searchEnterprise(@Param("keyword") String keyword);
	
	// 신고 대상 유형 검색 범위-일반회원
	List<EnterDeclaration> searchGeneralMember(@Param("keyword") String keyword);
	
	// 신고 사유 드롭다운 조회
	List<EnterDeclaration> getDeclarationReasonsByType(String dtCd);
	
	// 신고 타입 드롭다운 조회
	List<EnterDeclaration> getActiveDeclarationTypes();
}
