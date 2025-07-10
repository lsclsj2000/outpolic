package outpolic.enter.outsourcingRequest.service.impl;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import outpolic.enter.outsourcingRequest.domain.OutsourcingRequestDTO;
import outpolic.enter.outsourcingRequest.domain.ReplyDTO;
import outpolic.enter.outsourcingRequest.domain.RequestDetailDTO;
import outpolic.enter.outsourcingRequest.domain.RequestViewDTO;
import outpolic.enter.outsourcingRequest.mapper.OutsourcingRequestMapper;
import outpolic.enter.outsourcingRequest.service.OutsourcingRequestService;



@Service
@RequiredArgsConstructor
public class OutsourcingRequestServiceImpl  implements OutsourcingRequestService {
	
	private final OutsourcingRequestMapper requestMapper;
	
    // private final ContentListMapper contentListMapper;

    // 생성자를 통한 의존성 주입
  

    @Override
    public List<Map<String, Object>> searchEnterprises(String query) {
        return requestMapper.searchEnterprises(query);
    }

    @Override
    @Transactional
    public void createRequest(OutsourcingRequestDTO request) {
        // 1. 새로운 ocd_cd (요청코드)와 cl_cd (콘텐츠 목록 코드)를 생성
        // UUID 등을 활용하여 고유한 코드를 생성하는 것이 좋습니다.
        String newOcdCd = "OCD_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        // 요청 타입(문의/신청)에 따라 cl_cd 접두사 변경
        String prefix = "LIST_OS_"; // '신청'이 기본이라고 가정
        if ("문의".equals(request.getOcd_req_type())) {
            prefix = "LIST_PO_";
        }
        String newClCd = prefix + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // 2. content_list 테이블에 먼저 데이터 삽입 (필요한 경우)
        // ContentListDTO clDto = new ContentListDTO(newClCd, newOcdCd);
        // contentListMapper.insertContentList(clDto);

        // 3. 생성된 코드를 DTO에 담아서 최종 저장
        request.setOcd_cd(newOcdCd);
        request.setCl_cd(newClCd); // 이 부분은 content_list를 어떻게 관리하느냐에 따라 달라질 수 있습니다.
        
        requestMapper.insertRequest(request);
    }

    @Override
    public List<RequestViewDTO> getMyAllRequests(String userId) {
        return requestMapper.findAllRequestsByUserId(userId);
    }

    @Override
    public RequestDetailDTO getRequestWithReplies(String requestId) {
        // 1. 요청 원본 정보 조회
        RequestViewDTO requestInfo = requestMapper.findRequestDetailById(requestId);
        
        // 2. 해당 요청의 모든 답변 목록 조회
        List<ReplyDTO> replies = requestMapper.findRepliesByRequestId(requestId);
        
        // 3. 두 정보를 합쳐서 하나의 DTO로 만들어 반환
        RequestDetailDTO detailDTO = new RequestDetailDTO();
        detailDTO.setRequest(requestInfo);
        detailDTO.setReplies(replies);
        
        return detailDTO;
    }

    @Override
    @Transactional
    public void addReply(ReplyDTO reply) {
        // 답변 ID 생성, 답변자 정보 설정 등 추가 로직...
        requestMapper.insertReply(reply);
        // 답변이 추가되었으니, 원본 요청의 상태(stc_cd)를 '답변완료' 등으로 변경하는 로직 추가 가능
        // requestMapper.updateRequestStatus(reply.getRequestId(), "SD_REPLIED");
    }
    @Override
    public List<ReplyDTO> findRepliesRequestId(String requestId) {
        // TODO: 매퍼를 호출하여 답변 목록을 조회하는 로직을 구현해야 합니다.
        return requestMapper.findRepliesByRequestId(requestId);
    }
}
