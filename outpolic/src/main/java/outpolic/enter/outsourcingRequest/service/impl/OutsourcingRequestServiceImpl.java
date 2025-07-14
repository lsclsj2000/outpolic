package outpolic.enter.outsourcingRequest.service.impl;

import lombok.RequiredArgsConstructor;
import outpolic.enter.outsourcingRequest.domain.OutsourcingRequestDTO;
import outpolic.enter.outsourcingRequest.domain.RequestViewDTO;
import outpolic.enter.outsourcingRequest.mapper.EnterOutsourcingRequestMapper;
import outpolic.enter.outsourcingRequest.service.OutsourcingRequestService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service("enterOutsourcingRequestService") // <-- 빈 이름을 "enterOutsourcingRequestService"로 지정
@RequiredArgsConstructor
public class OutsourcingRequestServiceImpl implements OutsourcingRequestService {

    private final EnterOutsourcingRequestMapper requestMapper;

    @Override
    @Transactional
    public OutsourcingRequestDTO createRequest(OutsourcingRequestDTO request) {
        String latestOcdCd = requestMapper.findLatestOcdCd();
        int nextNum = 1;
        if (latestOcdCd != null && latestOcdCd.startsWith("OCD_C")) {
            try {
                nextNum = Integer.parseInt(latestOcdCd.substring(5)) + 1;
            } catch (NumberFormatException e) {
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

   
    // ★★★ 누락되었던 '받은 요청 조회' 메서드 구현 추가 ★★★
    @Override
    public List<RequestViewDTO> getReceivedRequests(String supplierEntCd) {
        return requestMapper.findReceivedRequests(supplierEntCd);
    }
    
    @Override
    public String findEntCdByMbrCd(String mbrCd) {
    	return requestMapper.findEntCdByMbrCd(mbrCd);
    }
    
    @Override
    public RequestViewDTO getRequestDetails(String requestId) {
    	return requestMapper.findRequestDetailById(requestId);
    }

	@Override
	public RequestViewDTO getRequestByDetails(String requestId) {
		// TODO Auto-generated method stub
		return requestMapper.findRequestDetailById(requestId);
	}
	
	@Override
    @Transactional
    public void updateRequestStatus(String requestId, String status) {
        // 1. 요청 테이블의 상태를 '승인' 또는 '거절'로 변경합니다.
        requestMapper.updateStatus(requestId, status);

        // 2. '승인'된 경우에만 진행 현황을 기록합니다.
        if ("SD_APPROVED".equals(status)) {
            
            String latestOspCd = requestMapper.findLatestOspCd();
            int nextNum = 1;

            // ▼▼▼ 이 부분을 수정합니다. ▼▼▼
            if (latestOspCd != null && latestOspCd.startsWith("OSP_C")) {
                try {
                    // 'OSP_C' (5글자) 다음부터가 숫자이므로, substring(5)로 수정합니다.
                    nextNum = Integer.parseInt(latestOspCd.substring(5)) + 1;
                } catch (NumberFormatException e) {
                    // 혹시 모를 예외에 대비해 기본값으로 설정
                    nextNum = 1; 
                }
            }
            
            // 새로운 코드를 생성할 때도 'OSP_C' 형식을 사용합니다.
            String newOspCd = String.format("OSP_C%05d", nextNum);
            // ▲▲▲ 수정 끝 ▲▲▲

            // requestMapper를 통해 outsourcing_prograss 테이블에 데이터를 추가합니다.
            requestMapper.insertInitialProgress(newOspCd, requestId, "SD_CONTRACT");
        }
    }

}
    
