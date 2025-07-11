package outpolic.enter.outsourcingRequest.domain;

import java.time.LocalDateTime;

import lombok.Data;
// 목록이나 상세보기에 사용할 DTO
@Data
public class RequestViewDTO {
	private String ocd_cd;
	private String ocd_req_type;
	private String ocd_ttl;
	private String demanderName;
	private String supplierName;
	private LocalDateTime ocd_dmnd_ymdt;
	private String	stc_cd;
}
