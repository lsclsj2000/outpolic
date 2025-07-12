package outpolic.enter.outsourcingRequest.service.impl;

import lombok.RequiredArgsConstructor;
import outpolic.enter.outsourcingRequest.domain.OutsourcingRequestDTO;
import outpolic.enter.outsourcingRequest.domain.RequestViewDTO;
import outpolic.enter.outsourcingRequest.mapper.OutsourcingRequestMapper;
import outpolic.enter.outsourcingRequest.service.OutsourcingRequestService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
// import java.util.UUID; // UUID 임포트는 더 이상 필요 없음

@Service
@RequiredArgsConstructor
public class OutsourcingRequestServiceImpl implements OutsourcingRequestService {

    private final OutsourcingRequestMapper requestMapper;

    @Override
    @Transactional
    public OutsourcingRequestDTO createRequest(OutsourcingRequestDTO request) {
        String latestOcdCd = requestMapper.findLatestOcdCd();
        int nextNum = 1;
        if (latestOcdCd != null && latestOcdCd.startsWith("OCD_C")) {
            try {
                nextNum = Integer.parseInt(latestOcdCd.substring(5)) + 1;
            } catch (NumberFormatException e) {
                // 파싱 실패 시, 기본값 1 또는 다른 로직으로 처리
                nextNum = 1;
            }
        }
        String newOcdCd = String.format("OCD_C%05d", nextNum);
        request.setOcd_cd(newOcdCd);

        requestMapper.insertRequest(request);
        return request;
    }

    @Override
    public List<RequestViewDTO> getSentRequests(String requesterId) {
        return requestMapper.findSentRequests(requesterId);
    }

    @Override
    public RequestViewDTO getRequestDetails(String requestId) {
        return requestMapper.findRequestDetailById(requestId);
    }
    
    @Override
    public List<RequestViewDTO> getReceivedRequests(String supplierEntCd) {
        return requestMapper.findReceivedRequests(supplierEntCd);
    }
}