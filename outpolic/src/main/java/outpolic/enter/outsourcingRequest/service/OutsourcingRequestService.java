package outpolic.enter.outsourcingRequest.service; // ✅ 패키지 이름 수정

import java.util.List;

import outpolic.enter.outsourcingRequest.domain.OutsourcingRequest;

public interface OutsourcingRequestService {
    void addOutsourcingRequest(OutsourcingRequest request);
    
    List<OutsourcingRequest> getReceivedRequests(String entCd);
    
    List<OutsourcingRequest> getSentRequests(String mbrCd);
    
    OutsourcingRequest getRequestDetail(String ocdCd);
    void updateRequestStatus(String ocdCd, String stcCd);
}