package outpolic.enter.outsourcingRequest.service;

import outpolic.enter.outsourcingRequest.domain.OutsourcingRequestDTO;
import outpolic.enter.outsourcingRequest.domain.RequestViewDTO;
import java.util.List;

public interface OutsourcingRequestService {

    /**
     * 외주 신청서 생성
     */
    OutsourcingRequestDTO createRequest(OutsourcingRequestDTO request);

    /**
     * 보낸 신청 목록 조회
     */
    List<RequestViewDTO> getSentRequests(String requesterId);

    /**
     * 신청 상세 내역 조회
     */
    RequestViewDTO getRequestDetails(String requestId);
}