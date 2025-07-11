package outpolic.enter.outsourcingRequest.domain;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 외주 신청 목록이나 상세 정보 표시에 사용될 DTO.
 */
@Data
public class RequestViewDTO {
    private String ocd_cd;
    private String ocd_ttl;
    private String demanderName;
    private String supplierName;
    private LocalDateTime ocd_dmnd_ymdt;
    private String stc_cd;

    // 권한 확인 및 상세 정보 조회를 위한 추가 필드
    private String mbr_cd;
    private String ent_cd;
    private String ocd_expln;
    private Long ocd_amt;
}