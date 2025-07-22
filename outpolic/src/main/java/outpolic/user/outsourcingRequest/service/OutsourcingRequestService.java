// [전체 수정] /user/outsourcingRequest/service/OutsourcingRequestService.java
package outpolic.user.outsourcingRequest.service;

import outpolic.enter.outsourcingRequest.domain.OutsourcingRequestDTO;
import outpolic.enter.outsourcingRequest.domain.RequestViewDTO;
import java.util.List;

public interface OutsourcingRequestService {

    OutsourcingRequestDTO createRequest(OutsourcingRequestDTO request);

    List<RequestViewDTO> getSentRequests(String requesterId);

    List<RequestViewDTO> getSentInquiries(String requesterId);

    RequestViewDTO getRequestDetails(String requestId);
    
    String findMbrCdByEntCd(String entCd); // 이 줄을 추가합니다.

}