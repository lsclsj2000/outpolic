package outpolic.admin.declaration.domain;

import java.util.List;

import lombok.Data;

@Data
public class AdminDeclaration {
	private String declarationCode;
	private String declarationMemberCode;
	private String declarationTargetCode;
	private String declarationRegYmdt;
	
	private String declarationReasonCode;
	private String declarationReasonName;

	private String declarationStcCode;
	private String declarationStcName;
	
	private String declarationResultCode;
	private String declarationResultName;
	
	private String declarationTypeCode;
	private String declarationTypeName;
	private String declarationTypeExpln;
	private String declarationTypeStcCode;
	private String declarationTypeStcName;
	private String declarationTypeRegAdmCode;
	private String declarationTypeRegYmdt;
	private String declarationTypeMdfcnAdmCode;
	private String declarationTypeMdfcnYmdt;
	
	private String drCode;
	private String drDtCode;
	private String drDtName;
	private String drName;
	private String drExpln;
	private String drStcCode;
	private String drStcName;
	private String drRegAdmCode;
	private String drRegYmdt;
	private String drMdfcnAdmCode;
	private String drMdfcnYmdt;
	
	private String drcCode;
	private String drcName;
	private String drcExpln;
	private String drcStcCode;
	private String drcStcName;
	private String drcRegAdmCode;
	private String drcRegYmdt;
	private String drcMdfcnAdmCode;
	private String drcMdfcnYmdt;
	
	
	private String declarationMemberName;
	
	// --- 첨부파일 목록 필드 추가 ---
	private List<SubmissionAttachment> attachments;

	private String declarationCn;
	private String declarationMdfcnAdmCode;
	private String declarationMdfcnYmdt;
	
	private String declarationProcessCode;
	private String declarationProcessContent;
	
    private String adminCode; // Controller에서 adm_cd를 설정하기 위해 추가 (필요시)

	// --- declaration_process 테이블의 수정자/수정일시 필드 추가 ---
	private String declarationProcessMdfcnAdmCode;
	private String declarationProcessMdfcnYmdt;
}
