package outpolic.user.review.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class UserOutsourcingReviewDTO {
    // 기본 정보 (DB: review 테이블)
    private String rvwCd;          // 리뷰 코드 (PK)
    private String oscId;          // 외주 계약 ID (FK)
    private String rvwCn;          // 리뷰 내용
    private BigDecimal rvwEvl;     // 리뷰 평점
    private LocalDateTime rvwRegYmdt; // 등록일
    private LocalDateTime rvwMdfcnYmdt; // 수정일

    // 리뷰 주체 및 대상 정보 (로직 처리에 필요)
    private String reviewerMbrCd;  // 리뷰 작성자 회원 코드 (DB: mbr_cd)
    private String revieweeEntCd;  // 리뷰 대상 기업 코드 (DB: ent_cd)

    // 목록 조회 시 사용 (Join 결과)
    private String entCd;          // 리뷰 대상 기업 코드
    private String mbrCd;          // 리뷰 작성자 회원 코드
    
    private String projectId; 
}
