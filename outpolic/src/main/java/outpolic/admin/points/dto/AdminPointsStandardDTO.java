package outpolic.admin.points.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;

@Data
public class AdminPointsStandardDTO {

    private String epCd;
    private String epExpln;
    private BigDecimal epAmt;
    private Timestamp epRegYmdt;
    private String epRegAdmCd;
    private Timestamp epMdfcnYmdt;
    private String epMdfcnAdmCd;
    private int epStatus;
}
