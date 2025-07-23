package outpolic.admin.settlement.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;

@Data
public class AdminSettlementDTO {

    // settlement
    private String stlmCd;
    private String mbrCd;
    private String gdsCd;
    private String stcCd;
    private int stlmCnt;
    private BigDecimal stlmAmt;
    private BigDecimal stlmUsedPoints;
    private BigDecimal stlmFinalAmt;
    private String stlmPaymentInfo;
    private String stlmCardNm;
    private String stlmProviderNm;
    private Timestamp stlmYmdt;

    // JOIN
    private String mbrNm;
    private String gdsNm;
    private String stcNm;
}
