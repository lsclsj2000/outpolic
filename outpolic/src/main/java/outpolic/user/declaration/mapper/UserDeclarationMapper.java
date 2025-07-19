package outpolic.user.declaration.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import outpolic.user.declaration.domain.UserDeclaration;
import outpolic.user.inquiry.domain.UserInquiryFile;

@Mapper
public interface UserDeclarationMapper {
	
	// 신고 등록
	int insertDeclaration(UserDeclaration declaration);

	// 신고 파일 업로드
	int insertDeclarationAttachments(List<UserInquiryFile> attachments);
	
	// 신고 대상 유형 검색 범위-포트폴리오
	List<UserDeclaration> searchPortfolio(@Param("keyword") String keyword);
	
	// 신고 대상 유형 검색 범위-외주
	List<UserDeclaration> searchOutsourcing(@Param("keyword") String keyword);
	
	// 신고 대상 유형 검색 범위-기업회원
	List<UserDeclaration> searchEnterprise(@Param("keyword") String keyword);
	
	// 신고 대상 유형 검색 범위-일반회원
	List<UserDeclaration> searchGeneralMember(@Param("keyword") String keyword);
	
	// 신고 사유 드롭다운 조회
	List<UserDeclaration> getDeclarationReasonsByType(String dtCd);
	
	// 신고 타입 드롭다운 조회
	List<UserDeclaration> getActiveDeclarationTypes();
}
