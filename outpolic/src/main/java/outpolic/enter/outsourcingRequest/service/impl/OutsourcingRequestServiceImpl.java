package outpolic.enter.outsourcingRequest.service.impl;

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
}

