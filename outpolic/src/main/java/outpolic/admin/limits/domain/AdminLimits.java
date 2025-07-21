package outpolic.admin.limits.domain;

import lombok.Data;

@Data
public class AdminLimits {
	private String limitsTypeCode;
	private String limitsTypeName;
	private String limitsTypeExpln;
	private String limitsTypeStcCode;
	private String limitsTypeStcName;
	private String limitsTypeRegAdmCode;
	private String limitsTypeRegYmdt;
	private String limitsTypeMdfcnAdmCode;
	private String limitsTypeMdfcnYmdt;
	
	private String limitsCode;
	private String limitsMemberCode;
	private String DeclarationTypeCode;
	private String DeclarationTypeName;
	private String DeclarationReasonCode;
	private String DeclarationReasonName;
	private String limitsStartYmdt;
	private String limitsEndYmdt;
	private String limitsClearYmdt;
	private Integer limitsRmdDays;
	private String limitsStatus;
	
	private String authorityMemberCode;
	private String authorityGrdCode;
	private Integer authorityPortfolio;
	private Integer authorityOsWrite;
	private Integer authorityOsContract;
	private Integer authorityOs;
	private Integer authorityChat;
	private Integer authorityReview;
	private Integer authorityMdfcnYmdt;
	
	
	private String limitsPeriodCode;
	private String limitsPeriodName;
	private Integer limitsPeriodDays;
	private String limitsPeriodMdfcnAdmCode;
	private String limitsPeriodMdfcnYmdt;
	private String limitsPeriodRegAdmCode;
	private String limitsPeriodRegYmdt;
	
	private String limitsReasonCode;
	private String limitsReasonCnd;
	private Integer limitsReasonCndMin;
	private Integer limitsReasonCndMax;
	private String limitsReasonExpln;
	private String limitsReasonRegAdmCode;
	private String limitsReasonRegYmdt;
	private String limitsReasonMdfcnAdmCode;
	private String limitsReasonMdfcnYmdt;
}
