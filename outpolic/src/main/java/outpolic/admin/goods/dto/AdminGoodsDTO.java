package outpolic.admin.goods.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class AdminGoodsDTO {
    private String gdsCd;
    private String gdsNm;
    private BigDecimal gdsAmt;
    private String gdsType;
    private int gdsPeriodQuantity;
    private String gdsUnit;
    private String gdsRegAdmCd;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Timestamp gdsRegYmdt;
    private String gdsMdfcnAdmCd;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Timestamp gdsMdfcnYmdt;
    private int gdsStatus;
}
