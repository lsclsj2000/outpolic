package outpolic.admin.points.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;

@Data
public class AdminPointsHistoryDTO {

	// points_status 테이블
    private String ptsCd;
    private String mbrCd;
    private BigDecimal ptsPoints;
    private BigDecimal ptsPointsDelta;
    private BigDecimal ptsCumPoints;
    private String ptsStatus;
    private Timestamp ptsYmdt;

    // member 테이블 (Join)
    private String mbrNm;
    
}
