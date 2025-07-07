package outpolic.enter.outsourcingRequest.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import outpolic.enter.outsourcingRequest.domain.OutsourcingRequest;
import outpolic.enter.outsourcingRequest.mapper.OutsourcingRequestMapper;


/*
@Service
@RequiredArgsConstructor
public class OutsourcingRequestServiceImpl  implements OutsourcingRequestService {
	private final OutsourcingRequestMapper requestMapper;
	
	@Override
	@Transactional
	public void addOutsourcingRequest(OutsourcingRequest  outsourcingRequest) {
		String latestOcdCd = requestMapper.findLatestOcdCd();
		int nextNum = (latestOcdCd == null) ? 1 : Integer.parseInt(latestOcdCd.substring(5))+1;
		String newOcdCd = String.format("OCD_C%05d",nextNum);
		outsourcingRequest.setOcdCd(newOcdCd);
		
		String newClCd = "LIST_"+ newOcdCd;
		requestMapper.insertContentListForRequest(newClCd, newOcdCd);
		outsourcingRequest.setClCd(newClCd);
		
		outsourcingRequest.setOcdReqType("신청");
		outsourcingRequest.setStcCd("SD_BEFORE");
		
		requestMapper.insertOutsourcingRequest(outsourcingRequest);
	}
}
*/
