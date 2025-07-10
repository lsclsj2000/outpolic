package outpolic.enter.outsourcingRequest.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import outpolic.enter.outsourcingRequest.domain.OutsourcingRequest;
import outpolic.enter.outsourcingRequest.mapper.OutsourcingRequestMapper;
import outpolic.enter.outsourcingRequest.service.OutsourcingRequestService;



@Service
@RequiredArgsConstructor
public class OutsourcingRequestServiceImpl  implements OutsourcingRequestService {
	
	private final OutsourcingRequestMapper requestMapper;
	
	@Override
	@Transactional // 모든 DB 작업이 하나의 묶음으로 처리되도록 보장
	public void addOutsourcingRequest(OutsourcingRequest request) {
		// 1. 새로운 요청 코드(PK) 생성 
		String latestOcdCd = requestMapper.findLatestOcdCd();
		int nextNum = (latestOcdCd == null) ? 1 : Integer.parseInt(latestOcdCd.substring(5))+1;
		String newOcdCd = String.format("OCD_C%05d",nextNum);
		request.setOcdCd(newOcdCd);
		
		// 2. 이 요청을 위한 content_list 레코드 설정
		String newClCd = "LIST_"+ newOcdCd;
		requestMapper.insertContentListForRequest(newClCd, newOcdCd);
		request.setClCd(newClCd);
		
		// 3. 초기 상태 설정 '신청', '승인 전'으로 설정합니다.
		request.setOcdReqType("신청");
		request.setStcCd("SD_BEFORE");
		
		// 4. DB에 최종 저장
		requestMapper.insertOutsourcingRequest(request);
		
		
	}
	
	@Override
	public List<OutsourcingRequest> getReceivedRequests(String entCd){
		return requestMapper.findReceivedRequestsByEntCd(entCd);
	}
	
	@Override
	public List<OutsourcingRequest> getSentRequests(String mbrCd){
		return requestMapper.findSentRequestsByMbrCd(mbrCd);
	}
	
	@Override
	public OutsourcingRequest getRequestDetail(String ocdCd) {
		return requestMapper.findRequestByOcdCd(ocdCd);
	}
	
	@Override
	@Transactional
	public void updateRequestStatus(String ocdCd, String stcCd) {
		// TODO: 상태 변경에 대한 권한 검사 로직이 필요할 수 있음 (예: 공급자만 승인/ 거절 가능)
		requestMapper.updateRequestStatus(ocdCd, stcCd);
	}
}

