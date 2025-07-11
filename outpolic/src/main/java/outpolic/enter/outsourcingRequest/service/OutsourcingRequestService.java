package outpolic.enter.outsourcingRequest.service; // ✅ 패키지 이름 수정

import java.util.List;
import java.util.Map;

import outpolic.enter.outsourcingRequest.domain.OutsourcingRequestDTO;
import outpolic.enter.outsourcingRequest.domain.ReplyDTO;
import outpolic.enter.outsourcingRequest.domain.RequestDetailDTO;
import outpolic.enter.outsourcingRequest.domain.RequestViewDTO;

public interface OutsourcingRequestService {
   /** 전문가(기업) 검색 */
	List<Map<String,Object>> searchEnterprises(String query);
	
	/** 새 외주 신청 또는 문의 생성 */
	void createRequest(OutsourcingRequestDTO request);
	
	/** 나의 모든 요청 목록 조회 */
	 List<RequestViewDTO> getMyAllRequests(String userId);
	 
	 /** 특정 요청 상세 정보와 모든 답변 조회 */
	 RequestDetailDTO getRequestWithReplies(String requestId);
	 
	 /** 새 답변 추가 */
	 void addReply(ReplyDTO reply);
	 
	 List<ReplyDTO> findRepliesRequestId(String requestId);
}