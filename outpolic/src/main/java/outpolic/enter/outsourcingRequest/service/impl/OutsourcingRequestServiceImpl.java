// [전체 코드] 채팅방 생성 로직 제외 및 모든 메소드 구현 버전
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
        int nextNum = 1;
        if (latestOcdCd != null && latestOcdCd.startsWith("OCD_C")) {
            try {
                nextNum = Integer.parseInt(latestOcdCd.substring(5)) + 1;
            } catch (NumberFormatException e) {
                logger.warn("Failed to parse latestOcdCd: {}", latestOcdCd, e);
            }
        }
        String newOcdCd = String.format("OCD_C%05d", nextNum);
        request.setOcd_cd(newOcdCd);

        // 이 부분만 남기고 chr_cd 관련 코드는 모두 삭제합니다.
        requestMapper.insertRequest(request);

        /* ▼▼▼ [삭제할 부분] chr_cd를 업데이트하는 아래 3줄의 코드를 삭제하거나 주석 처리하세요. ▼▼▼
        String newChrCd = "CHR_" + newOcdCd;
        requestMapper.updateChatRoomId(newOcdCd, newChrCd);
        request.setChr_cd(newChrCd);
        */

        return request;
    }
    
    @Override
    public List<RequestViewDTO> getSentInquiries(String requesterId) {
        return requestMapper.findSentInquiries(requesterId);
    }
    
    @Override
    public List<RequestViewDTO> getReceivedInquiries(String supplierEntCd){
    	return requestMapper.findReceivedInquiries(supplierEntCd);
    }
    
    @Override
    public List<RequestViewDTO> getSentRequests(String requesterId) {
        return requestMapper.findSentRequests(requesterId);
    }

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
    public String findMbrCdByEntCd(String entCd) {
        return requestMapper.findMbrCdByEntCd(entCd);
    }

	@Override
    @Transactional
    public void updateRequestStatus(String requestId, String status) {
		requestMapper.updateStatus(requestId, status);

        // '승인' 상태일 때만 외주 진행 현황 초기 데이터 생성
		if ("SD_APPROVED".equals(status)) {
            String latestOspCd = requestMapper.findLatestOspCd();
            int nextNum = 1;
            if (latestOspCd != null && latestOspCd.startsWith("OSP_C")) {
                try {
                    nextNum = Integer.parseInt(latestOspCd.substring(5)) + 1;
                } catch (NumberFormatException e) {
                    logger.warn("Failed to parse latestOspCd: {}", latestOspCd, e);
                }
            }
            
            List<String> stageStatusCodes = List.of("SD_CONTRACT", "SD_PLANNING", "SD_WORKPROGRESS", "SD_COMPLETION");
            List<Map<String, Object>> stageList = new ArrayList<>();
            for (String stageCode : stageStatusCodes) {
                Map<String, Object> stageData = new HashMap<>();
                String newOspCd = String.format("OSP_C%05d", nextNum++);
                
                stageData.put("ospCd", newOspCd);
                stageData.put("ocdCd", requestId);
                stageData.put("stcCd", stageCode);

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