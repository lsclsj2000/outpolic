package outpolic.enter.outsourcingRequest.domain;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 외주 신청/문의 목록이나 상세 정보 표시에 사용될 DTO
 */
@Data
public class RequestViewDTO {
    private String ocd_cd;
    private String ocd_ttl;
    private String demanderName; // 수요자 이름
    private String supplierName; // 공급자 이름
    private LocalDateTime ocd_dmnd_ymdt; // 요청일시
    private String stc_cd; // 상태 코드
    private String chr_cd; // 연결된 채팅방 ID

    // 상세 요청 내용
    private String ocd_expln; 
    private BigDecimal ocd_amt;     // 희망 예산
    private String ocd_frctn_cmdty; // 최종 제작물
    private String ocd_dlvgds_mthd; // 납품 방법
    private LocalDateTime ocd_strt_ymdt; // 희망 시작일
    private LocalDateTime ocd_dedline;   // 희망 종료일

    // [추가된 필드 1] 요청 타입을 구분하기 위한 필드 (신청/문의)
    private String ocd_req_type;

    // [추가된 필드 2] 공급자 기업 코드를 저장할 필드
    private String ent_cd;

    // [추가된 필드 3] 요청을 보낸 수요자의 회원 코드를 저장할 필드
    private String mbr_cd;

    // 참고용 필드 (컨트롤러에서 비교용으로 사용 가능)
    private String demanderMemberCode; // 요청 보낸 회원 코드 (mbr_cd와 동일)
    private String supplierMemberCode; // 요청 받은 기업의 대표 회원 코드
}