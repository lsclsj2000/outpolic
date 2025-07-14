package outpolic.enter.portfolioInquiry.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

/**
 * 포트폴리오 문의 목록이나 상세 정보 표시에 사용될 DTO 
 */

@Data
public class PortfolioInquiryViewDTO {
	 private String ocd_cd;
	    private String ocd_ttl;
	    private String demanderName; // 수요자 이름
	    private String supplierName; // 공급자 이름
	    
	    private LocalDateTime ocd_dmnd_ymdt; // 요청일시
	    private String stc_cd; // 상태 코드
	    private String chr_cd; // 연결된 채팅방 ID

	    // ★★★ 추가된 필드들 ★★★
	    private String ocd_frctn_cmdty; // 최종 제작물
	    private String ocd_dlvgds_mthd; // 납품 방법
	    private String ocd_expln;       // 상세 요청 내용
	    private BigDecimal ocd_amt;     // 희망 예산
	    private LocalDateTime ocd_strt_ymdt; // 희망 시작일
	    private LocalDateTime ocd_dedline;   // 희망 종료일
}
