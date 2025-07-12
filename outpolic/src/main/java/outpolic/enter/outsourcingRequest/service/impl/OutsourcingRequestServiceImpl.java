package outpolic.enter.outsourcingRequest.service.impl;

import lombok.RequiredArgsConstructor;
import outpolic.enter.outsourcingRequest.domain.OutsourcingRequestDTO;
import outpolic.enter.outsourcingRequest.domain.RequestViewDTO;
import outpolic.enter.outsourcingRequest.mapper.OutsourcingRequestMapper;
import outpolic.enter.outsourcingRequest.service.OutsourcingRequestService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID; // UUID 임포트

@Service
@RequiredArgsConstructor
public class OutsourcingRequestServiceImpl implements OutsourcingRequestService {

    private final OutsourcingRequestMapper requestMapper;

    @Override
    @Transactional
    public OutsourcingRequestDTO createRequest(OutsourcingRequestDTO request) {
        request.setOcd_cd("OCD_" + UUID.randomUUID().toString().substring(0, 10).toUpperCase());
		/*
		 * // ★추가: 채팅방 ID 생성 및 설정 (임시 로직, 실제로는 채팅방 서비스와 연동 필요) request.setChr_cd("CR_" +
		 * UUID.randomUUID().toString().substring(0, 10).toUpperCase());
		 */
        requestMapper.insertRequest(request);
        return request;
    }

    @Override
    public List<RequestViewDTO> getSentRequests(String requesterId) {
        return requestMapper.findSentRequests(requesterId);
    }

    @Override
    public RequestViewDTO getRequestDetails(String requestId) {
        // 여기에 실제로는 해당 사용자가 이 요청을 볼 권한이 있는지 확인하는 로직이 추가되어야 합니다.
        return requestMapper.findRequestDetailById(requestId);
    }
}