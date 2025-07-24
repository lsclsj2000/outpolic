package outpolic.systems.payment.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;


@Data
public class SettlementDTO {
	private String stlmCd;
	private String mbrCd;
	private String gdsCd;
	private String stcCd;
	private int stlmCnt;
	private BigDecimal stlmAmt;
    private Integer stlmUsedPoints;
    private BigDecimal stlmFinalAmt;
    private String stlmPaymentInfo;
    private String stlmCardNm;
    private String stlmProviderNm;
    private Timestamp stlmYmdt;
    private String stlmMethod;
    private String stlmCardType;
}