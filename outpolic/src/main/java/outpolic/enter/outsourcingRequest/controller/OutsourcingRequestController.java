package outpolic.enter.outsourcingRequest.controller;

import lombok.RequiredArgsConstructor;
import outpolic.enter.outsourcing.domain.EnterOutsourcing;
import outpolic.enter.outsourcing.service.EnterOutsourcingService;
import outpolic.enter.outsourcingRequest.domain.OutsourcingRequestDTO;
import outpolic.enter.outsourcingRequest.domain.RequestViewDTO;
import outpolic.enter.outsourcingRequest.service.OutsourcingRequestService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/enter/outsourcing-requests")
@RequiredArgsConstructor
public class OutsourcingRequestController {

    private final OutsourcingRequestService requestService;
    private final EnterOutsourcingService enterOutsourcingService;

    @GetMapping("/form/{osCd}")
    public String showRequestForm(@PathVariable String osCd, Model model) {
        EnterOutsourcing outsourcing = enterOutsourcingService.getOutsourcingByOsCd(osCd);
        model.addAttribute("outsourcing", outsourcing);
        return "enter/outsourcingRequest/requestForm";
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createRequest(
            @ModelAttribute OutsourcingRequestDTO requestDto,
            HttpSession session
            ) {
        String loggedInUserCode = (String) session.getAttribute("SCD");

        if (loggedInUserCode == null || loggedInUserCode.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", "로그인이 필요합니다."));
        }
        
        requestDto.setMbr_cd(loggedInUserCode);
        requestService.createRequest(requestDto);
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "외주 신청이 완료되었습니다."
        ));
    }
    
    /**
     * 내가 보낸 신청 목록 페이지를 보여주는 메서드 (HTML 뷰 반환)
     */
    @GetMapping("/sent")
    public String showSentRequests(Model model, HttpSession session) {
        String requesterCode = (String) session.getAttribute("SCD");

        if (requesterCode == null || requesterCode.isEmpty()) {
            return "redirect:/login";
        }
        
        List<RequestViewDTO> sentRequests = requestService.getSentRequests(requesterCode);
        model.addAttribute("requests", sentRequests);
        
        return "enter/outsourcingRequest/sentRequestsList";
    }

    /**
     * 보낸 외주 신청 목록 데이터를 JSON 형태로 반환하는 API 엔드포인트
     */
    @GetMapping("/api/sent")
    @ResponseBody
    public ResponseEntity<List<RequestViewDTO>> getSentRequestsApi(HttpSession session) {
        String requesterCode = (String) session.getAttribute("SCD");

        if (requesterCode == null || requesterCode.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<RequestViewDTO> sentRequests = requestService.getSentRequests(requesterCode);
        return ResponseEntity.ok(sentRequests);
    }

    /**
     * 외주 신청 상세 페이지를 보여주는 메서드
     * URL: /enter/outsourcing-requests/{requestId}
     */
    @GetMapping("/{requestId}")
    public String showRequestDetail(@PathVariable String requestId, Model model) {
        RequestViewDTO requestDetail = requestService.getRequestDetails(requestId);
        if (requestDetail == null) {
            return "redirect:/enter/outsourcing-requests/sent";
        }
        model.addAttribute("request", requestDetail);
        return "enter/outsourcingRequest/requestDetailView";
    }
    
    @GetMapping("/check-my-role")
    @ResponseBody
    public String checkMyRole(HttpSession session) {
        String username = (String) session.getAttribute("SID");
        String grade = (String) session.getAttribute("SGrd");

        if (username == null || username.isEmpty()) {
            return "<h2>로그인되지 않은 상태입니다.</h2>";
        }
        
        String authoritiesInfo = (grade != null) ?
                                 "ROLE_" + grade : "권한 없음";

        return "<h1>로그인 정보 확인</h1>" +
               "<h2>로그인 ID: " + username + "</h2>" +
               "<h2>현재 부여된 권한 (세션 기준): " + authoritiesInfo + "</h2>" +
               "<p>이 페이지가 보인다면, 로그인은 성공했으며 /enter/ 경로에 접근은 가능하다는 의미입니다.</p>" +
               "<p>만약 '현재 부여된 권한'에 'ROLE_USER' 또는 'ROLE_ENTER'가 없다면, 그것이 바로 문제입니다.</p>";
    }
    
    /**
     * 받은 외주 신청 목록 페이지 (HTML 뷰 반환)
     */
    @GetMapping("/received")
    public String showReceivedRequests(Model model, HttpSession session) {
        String loggedInEntCd = (String) session.getAttribute("SCD"); // 공급자는 보통 기업 코드로 식별 (예: EI_C00001)

        if (loggedInEntCd == null || loggedInEntCd.isEmpty()) {
            // 로그인하지 않았거나 기업 회원이 아니라면 리다이렉트
            return "redirect:/login"; // 또는 적절한 에러 페이지
        }
        // 이 뷰에서는 API를 통해 데이터를 로드하므로, 초기 모델에 데이터를 담지 않아도 됩니다.
        return "enter/outsourcingRequest/receivedRequestsList";
    }

    /**
     * 받은 외주 신청 목록 데이터를 JSON 형태로 반환하는 API 엔드포인트
     */
    @GetMapping("/api/received")
    @ResponseBody
    public ResponseEntity<List<RequestViewDTO>> getReceivedRequestsApi(HttpSession session) {
        String loggedInEntCd = (String) session.getAttribute("SCD"); // 로그인한 기업 코드

        if (loggedInEntCd == null || loggedInEntCd.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 여기서 실제 기업 코드를 가져오는 로직이 필요할 수 있습니다.
        // 로그인 시 SGRD가 "ENTER"인 경우 SCD에 기업 코드가 저장된다고 가정합니다.
        List<RequestViewDTO> receivedRequests = requestService.getReceivedRequests(loggedInEntCd);
        return ResponseEntity.ok(receivedRequests);
    }
}