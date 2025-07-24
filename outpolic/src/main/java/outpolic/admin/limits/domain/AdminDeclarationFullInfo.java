package outpolic.admin.limits.domain;

import lombok.Data;

@Data
public class AdminDeclarationFullInfo {
	private String declarationTypeCode;
	private String declarationReasonCode;
	private String declarationMemberCode;
	private String declarationTargetCode;
}
