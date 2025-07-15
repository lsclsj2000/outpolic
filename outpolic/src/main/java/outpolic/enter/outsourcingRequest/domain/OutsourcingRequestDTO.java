package outpolic.enter.outsourcingRequest.domain;

import lombok.Data;

/**
 * '외주 신청' 폼 데이터를 담는 DTO
 */
@Data
public class OutsourcingRequestDTO {
    // 시스템 설정 값
    private String ocd_cd;        // 요청 코드 (자동 생성)
    private String mbr_cd;        // 수요자(로그인한 사용자)의 회원 코드
    private String ent_cd;        // 공급자(게시물 주인)의 기업 코드
    private String cl_cd;         // 콘텐츠 목록 코드
    private String chr_cd;        // ★생성된 채팅방 ID (Service에서 채워줌)
    
    // 사용자 입력 값
    private String ocd_ttl;
    private String ocd_expln;
    private String ocd_frctn_cmdty;
    private String ocd_dlvgds_mthd;
    private String ocd_strt_ymdt;
    private String ocd_dedline;
    private Long ocd_amt;
    
    private String ocd_req_type;	// 요청 타입 ('신청' 또는 '문의')
}