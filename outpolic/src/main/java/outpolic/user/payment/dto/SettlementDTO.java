package outpolic.user.payment.dto;

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
	private String stlmPayType;
	private BigDecimal stlmAmt;
    private BigDecimal stlmUsedPoints;
    private BigDecimal stlmFinalAmt;
    private String stlmPaymentInfo;
    private String stlmCardNm;
    private String stlmProviderNm;
    private String stlmAccountInfo;
    private String stlmAccountNm;
    private Timestamp stlmYmdt;
    private boolean stlmAgreYn;
}