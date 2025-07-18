// [전체 수정] /user/outsourcingRequest/service/impl/OutsourcingRequestServiceImpl.java
package outpolic.user.outsourcingRequest.service.impl;

import lombok.RequiredArgsConstructor;
import outpolic.enter.outsourcingRequest.domain.OutsourcingRequestDTO;
import outpolic.enter.outsourcingRequest.domain.RequestViewDTO;
import outpolic.user.outsourcingRequest.mapper.UserOutsourcingRequestMapper;
import outpolic.user.outsourcingRequest.service.OutsourcingRequestService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service("userOutsourcingRequestService")
@RequiredArgsConstructor
public class OutsourcingRequestServiceImpl implements OutsourcingRequestService {

    private final UserOutsourcingRequestMapper requestMapper;

    @Override
    @Transactional
    public OutsourcingRequestDTO createRequest(OutsourcingRequestDTO request) {
        // 1. 새로운 요청 코드(ocd_cd) 생성
        String latestOcdCd = requestMapper.findLatestOcdCd();
        int nextNum = 1;
        if (latestOcdCd != null && latestOcdCd.startsWith("OCD_C")) {
            try {
                nextNum = Integer.parseInt(latestOcdCd.substring(5)) + 1;
            } catch (NumberFormatException e) {
                // 파싱 실패 시 기본값 1 유지
            }
        }
        String newOcdCd = String.format("OCD_C%05d", nextNum);
        request.setOcd_cd(newOcdCd);

        // 2. 요청 내역(outsourcing_contract_details) DB에 삽입
        requestMapper.insertRequest(request);

        // 3. [수정] 채팅방 ID만 생성하여 요청 내역 테이블에 업데이트
        String newChrCd = "CR_" + newOcdCd; // 채팅방 코드 생성 (실제 채팅방은 만들지 않음)
        requestMapper.updateChatRoomId(newOcdCd, newChrCd);
        request.setChr_cd(newChrCd);

        return request;
    }

    @Override
    public List<RequestViewDTO> getSentRequests(String requesterId) {
        return requestMapper.findSentRequests(requesterId);
    }

    @Override
    public List<RequestViewDTO> getSentInquiries(String requesterId){
        return requestMapper.findSentInquiries(requesterId);
    }

    @Override
    public RequestViewDTO getRequestDetails(String requestId) {
        return requestMapper.findRequestDetailById(requestId);
    }
}