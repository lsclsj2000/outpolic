package outpolic.admin.declaration.domain;

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
	
}
