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
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller("enterRequestController")
@RequestMapping("/enter/outsourcing-requests")
@RequiredArgsConstructor
public class EnterRequestController {

    private final OutsourcingRequestService requestService;
    private final EnterPortfolioService portfolioService;
    private final EnterOutsourcingService enterOutsourcingService;

    // [유지] 외주 '신청' 폼으로 이동
    @GetMapping("/form/{osCd}")
    public String showRequestForm(@PathVariable String osCd, Model model) {
        EnterOutsourcing outsourcing = enterOutsourcingService.getOutsourcingByOsCd(osCd);
        model.addAttribute("outsourcing", outsourcing);
        return "enter/outsourcingRequest/requestForm";
    }

    // [유지] 외주 '신청' 폼 제출
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
        requestDto.setOcd_req_type("신청");
        // 타입을 명확히 '신청'으로 설정
        requestService.createRequest(requestDto);
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "외주 신청이 완료되었습니다."
        ));
    }

    // [복원 및 유지] 포트폴리오 '문의' 작성 폼으로 이동
    @GetMapping("/inquiry-form/{prtfCd}")
    public String showInquiryForm(@PathVariable String prtfCd, Model model) {
        // 문의 대상 포트폴리오 정보를 조회하여 모델에 담아 전달
        model.addAttribute("portfolio", portfolioService.getPortfolioByPrtfCd(prtfCd));
        // ▼▼▼ 이 return 값이 "inquiryForm"을 정확히 가리키도록 수정합니다. ▼▼▼
        return "enter/portfolioInquiry/inquiryForm";
    }
    
    // [복원 및 유지] 작성된 포트폴리오 '문의' 전송
    @PostMapping("/inquiry/send")
    public String sendInquiry(@ModelAttribute OutsourcingRequestDTO requestDto, HttpSession session) {
    	String memberCode = (String) session.getAttribute("SCD");
        requestDto.setMbr_cd(memberCode);
    	
    	// 요청 타입을 '문의'로 직접 설정
    	requestDto.setOcd_req_type("문의");
    	
    	requestService.createRequest(requestDto);
        // 문의 목록 페이지로 리다이렉트
    	return "redirect:/enter/outsourcing-requests/sent-inquiries";
    }

    // [유지] 내가 보낸 '신청' 목록 페이지
    @GetMapping("/sent")
    public String showSentRequests(Model model, HttpSession session) {
        String requesterCode = (String) session.getAttribute("SCD");
        if (requesterCode == null || requesterCode.isEmpty()) {
            return "redirect:/login";
        }
        // 페이지 로딩 시 빈 화면을 보여주고, JS가 API를 통해 데이터를 채우도록 함
        return "enter/outsourcingRequest/sentRequestsList";
    }

    // [유지] 보낸 '신청' 목록 API
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
    
    // [유지] 내가 보낸 '문의' 목록 페이지
    @GetMapping("/sent-inquiries")
    public String showSentInquiries(Model model, HttpSession session) {
        model.addAttribute("listTitle", "보낸 문의 목록");
        String memberCode = (String) session.getAttribute("SCD");
        if(memberCode != null) {
            // 서비스가 DB에서 데이터를 조회합니다.
            List<RequestViewDTO> inquiries = requestService.getSentInquiries(memberCode);
            // 조회된 결과를 모델에 담아 View로 전달합니다.
            model.addAttribute("inquiries", inquiries);
        }
        // 이 View 파일로 렌더링합니다.
        return "enter/portfolioInquiry/inquiryList";
    }

    // [유지] 받은 '신청' 목록 페이지
    @GetMapping("/received")
    public String showReceivedRequests(Model model, HttpSession session) {
        String loggedInMbrCd = (String) session.getAttribute("SCD");
        if (loggedInMbrCd == null || loggedInMbrCd.isEmpty()) {
            return "redirect:/login";
        }
        return "enter/outsourcingRequest/receivedRequestsList";
    }

    // [유지] 받은 '신청' 목록 API
    @GetMapping("/api/received")
    @ResponseBody
    public ResponseEntity<List<RequestViewDTO>> getReceivedRequestsApi(HttpSession session) {
        String loggedInMbrCd = (String) session.getAttribute("SCD");
        if (loggedInMbrCd == null || loggedInMbrCd.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String supplierEntCd = requestService.findEntCdByMbrCd(loggedInMbrCd);
        List<RequestViewDTO> receivedRequests = requestService.getReceivedRequests(supplierEntCd);
        return ResponseEntity.ok(receivedRequests);
    }

    // [수정] 받은 '문의' 목록 페이지 (서버 사이드 렌더링을 위해 데이터 추가)
    @GetMapping("/received-inquiries")
    public String showReceivedInquiries(Model model, HttpSession session) {
    	model.addAttribute("listTitle","받은 포트폴리오 문의 목록");
        String loggedInMbrCd = (String) session.getAttribute("SCD"); // 세션에서 로그인된 회원 코드 가져오기 [cite: 33]

        if(loggedInMbrCd == null || loggedInMbrCd.isEmpty()) { // 로그인 여부 확인 [cite: 33]
            return "redirect:/login"; // 로그인되지 않았다면 로그인 페이지로 리다이렉트
        }

        String supplierEntCd = requestService.findEntCdByMbrCd(loggedInMbrCd); // 회원 코드로 기업 코드 조회 [cite: 33]
        if (supplierEntCd == null || supplierEntCd.isEmpty()) {
            // 해당 회원이 기업에 연결되어 있지 않다면 빈 목록을 보여주거나 에러 처리
            model.addAttribute("inquiries", List.of());
            return "enter/portfolioInquiry/receivedInquiryList";
        }

        List<RequestViewDTO> receivedInquiries = requestService.getReceivedInquiries(supplierEntCd); // 기업 코드로 받은 문의 목록 조회 [cite: 34]
        model.addAttribute("inquiries", receivedInquiries); // 조회된 목록을 모델에 추가 [cite: 23]

        return "enter/portfolioInquiry/receivedInquiryList"; // [cite: 32]
    }
    
    // [유지] 받은 '문의' 목록 API
    @GetMapping("/api/received-inquiries")
    @ResponseBody
    public ResponseEntity<List<RequestViewDTO>> getReceivedInquiriesApi(HttpSession session){
    	String loggedInMbrCd = (String) session.getAttribute("SCD");
        if(loggedInMbrCd == null || loggedInMbrCd.isEmpty()) {
    		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    	}
    	String supplierEntCd = requestService.findEntCdByMbrCd(loggedInMbrCd);
        List<RequestViewDTO> receivedInquiries = requestService.getReceivedInquiries(supplierEntCd);
    	return ResponseEntity.ok(receivedInquiries);
    }

    // [유지] 신청/문의 상세 페이지
    @GetMapping("/detail/{ocdCd}")
    public String showRequestDetail(@PathVariable("ocdCd") String ocdCd, Model model, HttpSession session) {
        RequestViewDTO requestDetail = requestService.getRequestDetails(ocdCd);
        if (requestDetail == null) {
            return "error/404";
        }
        model.addAttribute("request", requestDetail);
        String loggedInUserCode = (String) session.getAttribute("SCD");
        boolean isSupplier = loggedInUserCode != null && loggedInUserCode.equals(requestService.findMbrCdByEntCd(requestDetail.getEnt_cd()));
        model.addAttribute("isSupplier", isSupplier);
        // 목록으로 돌아가기 URL 설정
        String listUrl = "/";
        String reqType = requestDetail.getOcd_req_type();
        boolean isRequester = loggedInUserCode != null && loggedInUserCode.equals(requestDetail.getMbr_cd());
        
        if ("신청".equals(reqType)) {
            listUrl = isRequester ?
"/enter/outsourcing-requests/sent" : "/enter/outsourcing-requests/received";
        } else if ("문의".equals(reqType)) {
            listUrl = isRequester ?
"/enter/outsourcing-requests/sent-inquiries" : "/enter/outsourcing-requests/received-inquiries";
        }
        model.addAttribute("listUrl", listUrl);

        return "enter/outsourcingRequest/requestDetailView";
    }
    
    // [유지] 공급자의 신청 상태 변경 (승인/거절)
    @PostMapping("/update-status")
    @ResponseBody
    public ResponseEntity<Map<String,Object>> updateRequestStatus(@RequestBody Map<String,String> payload){
    	try {
    		String requestId = payload.get("requestId");
            String status = payload.get("status");
    		requestService.updateRequestStatus(requestId,status);
    		return ResponseEntity.ok(Map.of("success",true,"message","상태가 성공적으로 변경되었습니다."));
    	} catch(Exception e) {
    	    e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("success",false,"message",e.getMessage()));
        }
    }
}