package outpolic.enter.outsourcingRequest.domain;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
/**
 * 외주 신청 목록이나 상세 정보 표시에 사용될 DTO
 */
@Data
public class RequestViewDTO {
    private String ocd_cd;
    private String ocd_ttl;
    private String demanderName; // 수요자 이름
    private String supplierName; // 공급자 이름
    
    // ★★★ 추가: 실제 회원 코드를 저장할 필드 (컨트롤러에서 비교용) ★★★
    private String demanderMemberCode; // 요청을 보낸 회원의 코드
    private String supplierMemberCode; // 요청을 받은 기업의 회원 코드 (또는 기업 코드)

    private LocalDateTime ocd_dmnd_ymdt; // 요청일시
    private String stc_cd; // 상태 코드
    private String chr_cd; // 연결된 채팅방 ID

    // ★★★ 추가된 필드들 ★★★
    private String ocd_frctn_cmdty; // 최종 제작물
    private String ocd_dlvgds_mthd; // 납품 방법
    private String ocd_expln; // 상세 요청 내용
    private BigDecimal ocd_amt;     // 희망 예산
    private LocalDateTime ocd_strt_ymdt; // 희망 시작일
    private LocalDateTime ocd_dedline;   // 희망 종료일
    // --- 추가 필드 끝 ---
}