package outpolic.user.points.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;

@Data
public class UserPointsDTO {
    private String ptsCd;           	// 마일리지 코드
    private String mbrCd;          		// 회원 코드
    private BigDecimal ptsPoints;   	// 현재 마일리지
    private BigDecimal ptsPointsDelta; 	// 사용 / 적립 마일리지
    private BigDecimal ptsCumPoints; 	// 적용 후 마일리지 (최종 보유 마일리지)
    private String ptsStatus;       	// 구분 ('적립', '사용')
    private Timestamp ptsYmdt;      	// 최종 적용일시
}
