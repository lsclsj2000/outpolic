package outpolic.systems.payment.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;


@Data
public class SettlementDTO {
	private String stlmCd;				// 결제 코드
	private String mbrCd;				// 회원 코드
	private String gdsCd;				// 상품 코드
	private String stcCd;				// 상태 코드
	private int stlmCnt;				// 상품 개수
	private String stlmPayType;			// 결제 방식
	private BigDecimal stlmAmt;			// 결제 예정 금액
    private Integer stlmUsedPoints;		// 사용된 마일리지
    private BigDecimal stlmFinalAmt;	// 최종 결제 금액
    private String stlmPaymentInfo;		// 카드 결제 세부 정보
    private String stlmCardNm;			// 카드사
    private String stlmProviderNm;		// 간편결제사
    private String stlmAccountInfo;		// 계좌 세부 정보
    private String stlmAccountNm;		// 결제자
    private Timestamp stlmYmdt;			// 결제일시
    private boolean stlmAgreYn;			// 결제 약관동의 여부
}