package outpolic.user.review.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;

@Data
public class UserOutsourcingReviewDTO {
	private String rvwCd;         // 리뷰코드 (rvw_cd)
    private String oscId;         // 외주계약 ID (osc_id)
    private String mbrCd;         // 리뷰 작성자 회원코드 (mbr_cd)
    private String entCd;         // 리뷰 대상 기업코드 (ent_cd)
    private String rvwCn;         // 리뷰 내용 (rvw_cn)
    private BigDecimal rvwEvl;    // 리뷰 별점 (rvw_evl)
    private Timestamp rvwRegYmdt; // 리뷰 등록일시 (rvw_reg_ymdt)
    private Timestamp rvwMdfcnYmdt;
    
    
    // 추가 필드 (리뷰 저장 시 필요)
    private String reviewerMbrCd; // 리뷰 작성자 (로그인 세션에서 가져옴)
    private String revieweeEntCd; // 리뷰 대상 기업 (ocd_cd로 조회)
}
