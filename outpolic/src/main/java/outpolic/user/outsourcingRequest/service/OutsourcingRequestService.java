package outpolic.user.outsourcingRequest.service;

import outpolic.enter.outsourcingRequest.domain.OutsourcingRequestDTO;
import outpolic.enter.outsourcingRequest.domain.RequestViewDTO;
import java.util.List;

public interface OutsourcingRequestService {

    /**
     * 외주 신청서 생성 (User용)
     */
    OutsourcingRequestDTO createRequest(OutsourcingRequestDTO request);
    
    /**
     * 보낸 신청 목록 조회 (User용)
     */
    List<RequestViewDTO> getSentRequests(String requesterId);
    
    /**
     * 신청 상세 내역 조회 (User용)
     */
    RequestViewDTO getRequestDetails(String requestId);

    // getReceivedRequests(String) 메서드 선언이 없는 상태여야 합니다.
}