package outpolic.enter.outsourcingRequest.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import outpolic.enter.outsourcingRequest.domain.OutsourcingRequestDTO;
import outpolic.enter.outsourcingRequest.domain.ReplyDTO;
import outpolic.enter.outsourcingRequest.domain.RequestDetailDTO;
import outpolic.enter.outsourcingRequest.domain.RequestViewDTO;
import outpolic.enter.outsourcingRequest.service.OutsourcingRequestService;

@RestController
@RequestMapping("/enter")
@RequiredArgsConstructor
public class OutsourcingRequestController {
	
	private final OutsourcingRequestService requestService;
	
	/**
	*	전문가(기업) 검색 API
	*	@param query 사용자가 입력한 검색어
	*   @return 검색된 기업 목록 
	*/
	@GetMapping("/enterprises/search")
    public ResponseEntity<List<Map<String, Object>>> searchEnterprises(@RequestParam String query) {
        List<Map<String, Object>> enterprises = requestService.searchEnterprises(query);
        return ResponseEntity.ok(enterprises);
    }
	/**
     * 새 외주 신청 또는 포트폴리오 문의 제출 API
     * @param requestDto 폼에서 전송된 데이터
     * @return 성공 여부 및 메시지
     */
    @PostMapping("/requests")
    public ResponseEntity<Map<String, Object>> createRequest(OutsourcingRequestDTO requestDto) {
        // 실제로는 Spring Security 같은 인증 정보를 통해 로그인한 사용자 ID를 가져와야 합니다.
        // String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();
        // requestDto.setMbr_cd(currentUserId);
        
        requestService.createRequest(requestDto);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "요청이 성공적으로 제출되었습니다.");
        response.put("redirectUrl", "/enter/requests/sent"); // 성공 시 이동할 페이지
        
        return ResponseEntity.ok(response);
    }
    
    
    
    
    
    /**
     * 나의 모든 요청(보낸/받은) 목록 조회 API
     * @return 요청 목록
     */
    @GetMapping("/my/all-requests")
    public ResponseEntity<List<RequestViewDTO>> getMyAllRequests() {
        // 실제로는 Spring Security 같은 인증 정보를 통해 로그인한 사용자 ID를 가져와야 합니다.
        String myUserId = "EI_C00003"; // 예시: 현재 로그인한 사용자 ID
        
        List<RequestViewDTO> requests = requestService.getMyAllRequests(myUserId);
        return ResponseEntity.ok(requests);
    }

    /**
     * 특정 요청 상세 정보 및 답변 목록 조회 API
     * @param requestId 상세 조회할 요청의 ID (ocd_cd)
     * @return 요청 상세 정보 및 답변 목록
     */
    @GetMapping("/requests/{requestId}")
    public ResponseEntity<RequestDetailDTO> getRequestDetails(@PathVariable String requestId) {
        RequestDetailDTO requestDetails = requestService.getRequestWithReplies(requestId);
        return ResponseEntity.ok(requestDetails);
    }

    /**
     * 새 답변 등록 API
     * @param requestId 답변을 추가할 요청의 ID
     * @param replyDto 답변 내용
     * @return 저장된 답변 정보
     */
    @PostMapping("/requests/{requestId}/replies")
    public ResponseEntity<ReplyDTO> addReply(@PathVariable String requestId, @RequestBody ReplyDTO replyDto) {
        // 실제로는 로그인한 사용자 ID를 답변자 ID로 설정해야 합니다.
        // String replierId = ...;
        // replyDto.setReplierId(replierId);
        
        replyDto.setRequestId(requestId);
        requestService.addReply(replyDto);
        
        return ResponseEntity.ok(replyDto); // 저장 후 처리된 정보 반환
    }
}