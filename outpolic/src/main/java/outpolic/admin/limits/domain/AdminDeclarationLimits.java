package outpolic.admin.limits.domain;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class AdminDeclarationLimits {
	private String lmtCd;
    private String mbrCd;
    private String dpCd;
    private String drCd;
    private String lrCd;
    private Timestamp lmtStartYmdt;
    private Timestamp lmtEndYmdt;
    private Timestamp lmtClearYmdt;
    private int lmtRmdDays;
    private String lmtDockRsn;
    private String lmtRegAdmCd;
}
