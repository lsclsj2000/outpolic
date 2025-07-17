package outpolic.enter.outsourcingRequest.controller;
import lombok.RequiredArgsConstructor;
import outpolic.enter.outsourcing.domain.EnterOutsourcing;
import outpolic.enter.outsourcing.service.EnterOutsourcingService;
import outpolic.enter.outsourcingRequest.domain.OutsourcingRequestDTO;
import outpolic.enter.outsourcingRequest.domain.RequestViewDTO;
import outpolic.enter.outsourcingRequest.service.OutsourcingRequestService;
import outpolic.enter.portfolio.service.EnterPortfolioService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
@Controller("enterOutsourcingRequestController")
@RequestMapping("/enter/outsourcing-requests")
@RequiredArgsConstructor
public class OutsourcingRequestController {

    private final OutsourcingRequestService requestService;
    private final EnterPortfolioService portfolioService;
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
    @GetMapping("/detail/{ocdCd}")
    public String showRequestDetail(@PathVariable("ocdCd") String ocdCd, Model model, HttpServletRequest request, HttpSession session) {

        RequestViewDTO requestDetail = requestService.getRequestDetails(ocdCd);
        model.addAttribute("request", requestDetail);

        String loggedInUserCode = (String) session.getAttribute("SCD"); // 현재 로그인한 사용자 코드

        // ★★★ 수정된 부분: 로그인한 사용자가 요청의 공급자인지 확인하는 로직 ★★★
        boolean isSupplier = false;
        if (loggedInUserCode != null && requestDetail.getSupplierMemberCode() != null) {
            isSupplier = loggedInUserCode.equals(requestDetail.getSupplierMemberCode());
        }
        model.addAttribute("isSupplier", isSupplier);
        // ★★★ 수정 완료 ★★★


        String referrer = request.getHeader("Referer");
        String listUrl = (referrer != null && referrer.contains("/sent"))
                       ? "/enter/outsourcing-requests/sent"
                       : "/enter/outsourcing-requests/received";
        model.addAttribute("listUrl", listUrl);

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
        
        String authoritiesInfo = (grade != null) ? "ROLE_" + grade : "권한 없음";

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
        String loggedInMbrCd = (String) session.getAttribute("SCD");
        // 변경: entCd 대신 mbrCd를 사용하여 기업 코드 조회

        if (loggedInMbrCd == null || loggedInMbrCd.isEmpty()) {
            return "redirect:/login";
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
        String loggedInMbrCd = (String) session.getAttribute("SCD");
        if (loggedInMbrCd == null || loggedInMbrCd.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String supplierEntCd = requestService.findEntCdByMbrCd(loggedInMbrCd);

        // ▼▼▼ 이 부분의 파라미터를 'supplierEntCd'로 수정합니다.
        List<RequestViewDTO> receivedRequests = requestService.getReceivedRequests(supplierEntCd);
        
        return ResponseEntity.ok(receivedRequests);
    }
    
    @PostMapping("/update-status")
    @ResponseBody
    public ResponseEntity<Map<String,Object>> updateReqeuestStatus(@RequestBody Map<String,String> payload){
    	try {
    		String requestId = payload.get("requestId");
            String status = payload.get("status");
    		requestService.updateRequestStatus(requestId,status);
    		return ResponseEntity.ok(Map.of("success",true,"message","상태가 성공적으로 변경되었습니다."));
    	
    }catch(Exception e) {
    	e.printStackTrace();
    	return ResponseEntity.badRequest().body(Map.of("success",false,"message",e.getMessage()));
    }
    }
    	
    /**
     * 포트폴리오 문의 작성 폼을 보여주는 메서드	
     */
    @GetMapping("/inquiry-form/{prtfCd}")
    public String showInquiryForm(@PathVariable String prtfCd, Model model) {
    	// 문의 대상 포트폴리오 정보를 조회하여 모델에 담아 전달
    	model.addAttribute("portfolio",portfolioService.getPortfolioByPrtfCd(prtfCd));
    	return "enter/portfolioInquiry/inquiryForm";
    	
    }
    
    /** * 작성된 포트폴리오 문의를 서버로 전송하여 저장하는 메서드
     */
    @PostMapping("/inquiry/send")
    public String sendInquiry(@ModelAttribute OutsourcingRequestDTO requestDto, HttpSession session) {
    	String memberCode = (String) session.getAttribute("SCD");
        requestDto.setMbr_cd(memberCode);
    	
    	// 요청 타입을 '문의'로 직접 설정
    	requestDto.setOcd_req_type("문의");
    	
    	requestService.createRequest(requestDto);
    	
    	return "redirect:/enter/outsourcing-requests/sent-inquiries";
    }
    /**
     * 보낸 문의 목록을 보여주는 페이지 (기존 '보낸 신청'과 구분)
     */
    @GetMapping("/sent-inquiries")
    public String showSentInquiries(Model model, HttpSession session) {
        // 이 부분은 나중에 '문의' 타입만 필터링하는 서비스 메서드를 만들어 연결해야 합니다.
        // 우선은 페이지만 연결합니다.
        model.addAttribute("listTitle", "보낸 문의 목록");
        return "enter/portfolioInquiry/inquiryList";
    }
}