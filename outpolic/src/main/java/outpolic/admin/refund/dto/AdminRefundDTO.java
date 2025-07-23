package outpolic.admin.refund.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;
import lombok.Data;

@Data
public class AdminRefundDTO {

    // refundment 테이블
    private String rfndCd;
    private String stlmCd;
    private String mbrCd;
    private BigDecimal rfndAmt;
    private Timestamp rfndProcYmdt;

    // JOIN된 테이블
    private String mbrNm;
    private BigDecimal stlmUsedPoints;
}