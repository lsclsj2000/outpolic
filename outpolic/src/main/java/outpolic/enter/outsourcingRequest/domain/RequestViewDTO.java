package outpolic.enter.outsourcingRequest.domain;

import lombok.Data;
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
    
    private LocalDateTime ocd_dmnd_ymdt; // 요청일시
    private String stc_cd; // 상태 코드
    private String chr_cd; // ★연결된 채팅방 ID
}