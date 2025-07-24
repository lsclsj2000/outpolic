package outpolic.user.settlement.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;

@Data
public class UserSettlement {
	private String stlmCd;
    private String gdsCd;
    private String stcCd;
    private int stlmCnt;
    private Integer stlmUsedPoints;
    private BigDecimal stlmFinalAmt;
    private Timestamp stlmYmdt;
    private String stlmMethod;
    private String stlmCardType;

    // JOIN을 통해 가져올 추가 정보
    private String gdsNm;      // goods 테이블의 상품명
    private String stcNm;      // status_code 테이블의 상태명
}
