package outpolic.enter.outsourcingRequest.domain;

import lombok.Data;
import java.util.List;

/**
 * 신청 상세 페이지의 모든 데이터를 묶어서 전달하는 DTO.
 */
@Data
public class RequestDetailDTO {
    private RequestViewDTO request;
    private List<ReplyDTO> replies;
}