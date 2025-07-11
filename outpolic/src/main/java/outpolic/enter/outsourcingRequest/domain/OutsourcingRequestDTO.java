package outpolic.enter.outsourcingRequest.domain;

import lombok.Data;

/**
 * '외주 신청' 폼에서 전송된 데이터를 담기 위한 DTO.
 * outsourcing_contract_details 테이블의 컬럼들과 매핑됩니다.
 */
@Data
public class OutsourcingRequestDTO {
    // 시스템에서 생성/설정되는 값
    private String ocd_cd;        // 요청 코드 (서비스에서 UUID로 생성)
    private String ocd_req_type;  // 요청 타입 ('신청'으로 고정)
    private String mbr_cd;        // 수요자(로그인한 사용자)의 회원 코드
    private String ent_cd;        // 공급자(게시물 주인)의 기업 코드
    private String cl_cd;         // 콘텐츠 목록 코드

    // 사용자가 폼에서 직접 입력하는 값
    private String ocd_ttl;         // 요청 제목
    private String ocd_expln;       // 상세 요청 내용
    private String ocd_frctn_cmdty; // 최종 제작물
    private String ocd_dlvgds_mthd; // 납품 방법
    private String ocd_dedline;     // 희망 작업 기한
    private Long ocd_amt;           // 희망 예산
}