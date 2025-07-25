package outpolic.admin.limits.domain;



import java.sql.Timestamp;

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
	private Timestamp limitsStartYmdt;
	private Timestamp limitsEndYmdt;
	private String limitsClearYmdt;
	private int limitsRmdDays;
	private String limitsStatus;
	
	private String authorityMemberCode;
	private String authorityGrdCode;
	private int authorityPortfolio;
	private int authorityOsWrite;
	private int authorityOsContract;
	private int authorityOs;
	private int authorityChat;
	private int authorityReview;
	private int authorityMdfcnYmdt;
	
	
	private String limitsPeriodCode;
	private String limitsPeriodName;
	private int limitsPeriodDays;
	private String limitsPeriodMdfcnAdmCode;
	private String limitsPeriodMdfcnYmdt;
	private String limitsPeriodRegAdmCode;
	private String limitsPeriodRegYmdt;
	
	private String limitsReasonCode;
	private String limitsReasonCnd;
	private int limitsReasonCndMin;
	private int limitsReasonCndMax;
	private String limitsReasonExpln;
	private String limitsReasonRegAdmCode;
	private String limitsReasonRegYmdt;
	private String limitsReasonMdfcnAdmCode;
	private String limitsReasonMdfcnYmdt;
	
	private String limitsReasonCondition;
	private int limitsReasonMinCount;
	private int limitsReasonMaxCount;
	
	private String lmtCd;
    private String mbrCd;
	private String dpCd;
	private String drCd;
	private String lrCd;
	private Timestamp lmtStartYmdt;
	private Timestamp lmtEndYmdt;
	private Timestamp lmtClearYmdt;
	private Integer lmtRmdDays;
	private String lmtDockRsn;
	private String lmtRegAdmCd;
}
