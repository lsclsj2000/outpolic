package outpolic.systems.refund.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;

@Data
public class RefundDTO {
	private String rfndCd;
    private String stlmCd;
    private String mbrCd;
    private String stcCd;
    private BigDecimal stlmFinalAmt;
    private Timestamp stlmYmdt;
}
