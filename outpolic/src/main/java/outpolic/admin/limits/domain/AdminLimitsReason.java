package outpolic.admin.limits.domain;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class AdminLimitsReason {
	private String limitsReasonCode;
	private String declarationTypeCode;
	private String declarationReasonCode;
	private String limitsPeriodCode;
	private String limitsReasonCnd;
	private int limitsReasonCndMin;
	private int limitsReasonCndMax;
	private String limitsReasonExpln;
	private String limitsReasonRegAdmCode;
	private Timestamp limitsReasonRegYmdt;
	private String limitsReasonMdfcnAdmCode;
	private Timestamp limitsReasonMdfcnYmdt;
}
