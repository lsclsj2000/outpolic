package outpolic.admin.declaration.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import outpolic.admin.declaration.domain.AdminDeclaration;
import outpolic.admin.declaration.domain.SubmissionAttachment;

@Mapper
public interface AdminDeclarationMapper {
	
	// 신고 처리 내역 첨부파일 목록 조회
	List<SubmissionAttachment> getSubmissionAttachmentsByDeclarationCode(@Param("declarationCode") String declarationCode);
	
	// 신고 처리 내역의 내용 및 결과 업데이트
	void updateDeclarationProcessContentAndResult(AdminDeclaration adminDeclaration);

	// 신고 테이블의 상태 및 수정자/수정일시 업데이트 (이름 변경)
	void updateDeclarationStatusAndModifier(AdminDeclaration adminDeclaration);
	
	// 신고 내역 수정 시 처리 pk 생성
	void insertDeclarationProcess(AdminDeclaration adminDeclaration);
	
	// 신고 내역 수정 업데이트
	void updateDeclaration(AdminDeclaration adminDeclaration);
	
	// 신고 처리 상태 조회
	List<AdminDeclaration> getDeclarationStatusList();
	
	// 신고 타입별 신고 사유 조회
	List<AdminDeclaration> getDeclarationReasonsByTypeCode(String dtCode);
	
	// 신고 수정팝업창 조회
	AdminDeclaration getAdminDeclarationDetail(@Param("declarationCode") String declarationCode);
	
	// 신고 사유 수정팝업창 조회
	AdminDeclaration getDeclarationReasonByCode(String code);
	
	// 신고처리결과코드 수정팝업창 조회
	AdminDeclaration getDeclarationResultByCode(String code);
	
	// 신고 사유 수정
	void updateDeclarationReason(AdminDeclaration adminDeclaration);
	
	// 신고처리결과코드 수정
	void updateDeclarationResult(AdminDeclaration adminDeclaration);
	
	// 신고 타입 수정팝업창 조회
	AdminDeclaration getDeclarationTypeByCode(String code);
	
	// 신고 타입 수정
	void updateDeclarationType(AdminDeclaration adminDeclaration);
	
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

    // 신고 처리 내역 상세 조회 (이 메서드는 getAdminDeclarationDetail에서 dp_cd를 가져오므로 필요 없을 수 있습니다.)
    // AdminDeclaration getDeclarationProcessDetailByDeclarationCode(@Param("declCd") String declCd);
}
