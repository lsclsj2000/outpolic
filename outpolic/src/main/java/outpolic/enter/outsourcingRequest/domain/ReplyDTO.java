package outpolic.enter.outsourcingRequest.domain;

import java.time.LocalDateTime;

import lombok.Data;
// 답변 데이터를 주고받기 위한 DTO
@Data
public class ReplyDTO {
	private String requestId;
	private String replierId;
	private String replierName;
	private String content;
	private LocalDateTime date;
}
