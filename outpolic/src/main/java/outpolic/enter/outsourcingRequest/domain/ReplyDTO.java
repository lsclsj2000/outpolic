package outpolic.enter.outsourcingRequest.domain;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 신청 건에 대한 답변(메시지) 정보를 담는 DTO.
 */
@Data
public class ReplyDTO {
    private String replyId;
    private String requestId;
    private String replierId;
    private String replierName;
    private String content;
    private LocalDateTime date;
}