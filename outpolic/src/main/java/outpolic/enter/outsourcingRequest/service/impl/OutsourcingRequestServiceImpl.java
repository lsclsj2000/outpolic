package outpolic.enter.outsourcingRequest.service.impl;

import lombok.RequiredArgsConstructor;
import outpolic.enter.outsourcingRequest.domain.OutsourcingRequestDTO;
import outpolic.enter.outsourcingRequest.domain.RequestViewDTO;
import outpolic.enter.outsourcingRequest.mapper.EnterOutsourcingRequestMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime; // LocalDateTime 임포트 추가 
import java.util.ArrayList; // ArrayList 임포트 추가 
import java.util.HashMap; // HashMap 임포트 추가 
import java.util.List; // List 임포트 추가 
import java.util.Map; // Map 임포트 추가 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service("enterOutsourcingRequestService")
@RequiredArgsConstructor
public class OutsourcingRequestServiceImpl implements outpolic.enter.outsourcingRequest.service.OutsourcingRequestService {

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

        requestMapper.insertRequest(request); 
        
        // 채팅방 ID 업데이트 로직은 그대로 유지 (기존에는 주석처리되었으나, 필요하다면 활성화)
//        String newChrCd = "CHR_" + newOcdCd;
//        requestMapper.updateChatRoomId(newOcdCd, newChrCd);
//        request.setChr_cd(newChrCd);
        
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
        // 1. 요청의 현재 타입 조회
        RequestViewDTO currentRequest = requestMapper.findRequestDetailById(requestId);
        if (currentRequest == null) {
            throw new IllegalArgumentException("요청을 찾을 수 없습니다: " + requestId);
        }

        // 2. 요청 상태 업데이트 
        requestMapper.updateStatus(requestId, status); 

        // 3. '신청' 타입이 '승인'되었을 경우에만 outsourcing_prograss에 데이터 삽입 
        // 사용자의 요청에 따라 '문의'는 outsourcing_progress에 들어가지 않도록 명확히 분리 
        if ("신청".equals(currentRequest.getOcd_req_type()) && "SD_APPROVED".equals(status)) { 
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
        // '문의' 타입의 승인/거절은 단순히 상태만 변경하고, outsourcing_prograss에는 영향을 주지 않습니다. 
    }
}