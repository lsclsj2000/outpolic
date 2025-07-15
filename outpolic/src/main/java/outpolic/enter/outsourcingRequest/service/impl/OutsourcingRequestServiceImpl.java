package outpolic.enter.outsourcingRequest.service.impl;

import lombok.RequiredArgsConstructor;
import outpolic.enter.outsourcingRequest.domain.OutsourcingRequestDTO;
import outpolic.enter.outsourcingRequest.domain.RequestViewDTO;
import outpolic.enter.outsourcingRequest.mapper.EnterOutsourcingRequestMapper;
import outpolic.enter.outsourcingRequest.service.OutsourcingRequestService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service("enterOutsourcingRequestService")
@RequiredArgsConstructor
public class OutsourcingRequestServiceImpl implements OutsourcingRequestService {

    private static final Logger logger = LoggerFactory.getLogger(OutsourcingRequestServiceImpl.class);
    private final EnterOutsourcingRequestMapper requestMapper;
    @Override
    @Transactional
    public OutsourcingRequestDTO createRequest(OutsourcingRequestDTO request) {
        String latestOcdCd = requestMapper.findLatestOcdCd();
        int nextNum = 1; // 기본값 1
        if (latestOcdCd != null && latestOcdCd.startsWith("OCD_C")) {
            try {
                nextNum = Integer.parseInt(latestOcdCd.substring(5)) + 1;
            } catch (NumberFormatException e) {
                logger.warn("Failed to parse latestOcdCd: {}", latestOcdCd, e);
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
        
        // 2. '승인'된 경우에만 4개의 진행 단계를 생성합니다.
        if ("SD_APPROVED".equals(status)) {
            
        	// 3. 새로운 진행 코드(osp_cd)의 시작 번호를 계산합니다.
            String latestOspCd = requestMapper.findLatestOspCd();
            int nextNum = 1; // 기본값 1
            if (latestOspCd != null && latestOspCd.startsWith("OSP_C")) {
                try {
                    nextNum = Integer.parseInt(latestOspCd.substring(5)) + 1;
                } catch (NumberFormatException e) {
                    logger.warn("Failed to parse latestOspCd: {}", latestOspCd, e);
                }
            }
            // 4. 추가할 4개의 진행 단계 상태 코드를 정의합니다. 
            List<String> stageStatusCodes = List.of("SD_CONTRACT", "SD_PLANNING", "SD_WORKPROGRESS", "SD_COMPLETION");
            // 5. DB에 한 번에 INSERT할 리스트를 준비합니다. 
            List<Map<String, Object>> stageList = new ArrayList<>();
            for (String stageCode : stageStatusCodes) {
                Map<String, Object> stageData = new HashMap<>();
                // 1. 새로운 osp_cd를 생성합니다.
                String newOspCd = String.format("OSP_C%05d", nextNum++);
                
                // 2. 생성한 newOspCd를 stageData Map에 추가합니다.
                stageData.put("ospCd", newOspCd);
                stageData.put("ocdCd", requestId);
                stageData.put("stcCd", stageCode);
                
                
                // 5-2. 첫단계인 '계약 체결(SD_CONTRACT)'에만 완료 표시와 시간을 기록합니다.
                if ("SD_CONTRACT".equals(stageCode)) {
                    stageData.put("ospSplyYmdt", LocalDateTime.now());
                    stageData.put("ospCustYn", 1);
                } else {
                    stageData.put("ospSplyYmdt", null);
                    stageData.put("ospCustYn", 0);
                }
                
                stageList.add(stageData);
            }

            if (!stageList.isEmpty()) {
                requestMapper.insertInitialProgressStages(stageList);
            }
        }
    }
}