// [전체 수정] /user/outsourcingRequest/controller/OutsourcingRequestController.java

package outpolic.user.outsourcingRequest.controller;

import lombok.RequiredArgsConstructor;
import outpolic.enter.outsourcing.domain.EnterOutsourcing;
import outpolic.enter.outsourcing.service.EnterOutsourcingService;
import outpolic.enter.outsourcingRequest.domain.OutsourcingRequestDTO;
import outpolic.enter.outsourcingRequest.domain.RequestViewDTO;
import outpolic.user.outsourcingRequest.service.OutsourcingRequestService;
import outpolic.enter.portfolio.domain.EnterPortfolio;
import outpolic.enter.portfolio.service.EnterPortfolioService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

@Controller("userOutsourcingRequestController")
@RequestMapping("/user/outsourcing-requests")
@RequiredArgsConstructor
public class OutsourcingRequestController {

    private final OutsourcingRequestService outsourcingRequestService;
    private final EnterOutsourcingService enterOutsourcingService;
    private final EnterPortfolioService enterPortfolioService;

    // 외주 '신청' 폼 페이지
    @GetMapping("/form/{osCd}")
    public String showRequestForm(@PathVariable String osCd, Model model) {
        EnterOutsourcing outsourcing = enterOutsourcingService.findOutsourcingDetailsByOsCd(osCd);
        if (outsourcing == null) {
            return "redirect:/user/outsourcing/list";
        }
        model.addAttribute("outsourcing", outsourcing);
        return "user/outsourcingRequest/requestForm";
    }

    // 포트폴리오 '문의' 폼 페이지
    @GetMapping("/inquiry-form/{prtfCd}")
    public String showInquiryForm(@PathVariable String prtfCd, Model model, HttpSession session) {
        if (session.getAttribute("SCD") == null) {
            return "redirect:/login";
        }
        EnterPortfolio portfolio = enterPortfolioService.getPortfolioByPrtfCd(prtfCd);
        if (portfolio == null) {
            return "redirect:/portfolio/list?error=notfound";
        }
        model.addAttribute("portfolio", portfolio);
        return "user/portfolioInquiry/inquiryForm";
    }


    // '신청' 및 '문의' 제출 처리 통합
    @PostMapping
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createRequest(
            @ModelAttribute OutsourcingRequestDTO requestDto,
            HttpSession session) {
        
        String loggedInUserCode = (String) session.getAttribute("SCD");
        if (loggedInUserCode == null || loggedInUserCode.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", "로그인이 필요합니다."));
        }
        
        requestDto.setMbr_cd(loggedInUserCode);
        
        outsourcingRequestService.createRequest(requestDto);
        
        boolean isInquiry = "문의".equals(requestDto.getOcd_req_type());
        String message = isInquiry ? "문의가 성공적으로 전송되었습니다." : "외주 신청이 완료되었습니다.";
        String redirectUrl = isInquiry ? "/user/outsourcing-requests/sent-inquiries" : "/user/outsourcing-requests/sent";
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", message,
            "redirectUrl", redirectUrl
        ));
    }

    // 보낸 외주 신청 목록 페이지
    @GetMapping("/sent")
    public String showSentRequests(Model model, HttpSession session) {
        String requesterCode = (String) session.getAttribute("SCD");
        if (requesterCode == null || requesterCode.isEmpty()) {
            return "redirect:/login";
        }
        return "user/outsourcingRequest/sentRequestsList";
    }

    // 보낸 외주 신청 목록 API
    @GetMapping("/api/sent")
    @ResponseBody
    public ResponseEntity<List<RequestViewDTO>> getSentRequestsApi(HttpSession session) {
        String requesterCode = (String) session.getAttribute("SCD");
        if (requesterCode == null || requesterCode.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<RequestViewDTO> sentRequests = outsourcingRequestService.getSentRequests(requesterCode); 
        return ResponseEntity.ok(sentRequests);
    }
    
    // 보낸 문의 목록 페이지
    @GetMapping("/sent-inquiries")
    public String showSentInquiries(Model model) {
        return "user/outsourcingRequest/sentInquiriesList";
    }
    
    // 보낸 문의 목록 API
    @GetMapping("/api/sent-inquiries")
    @ResponseBody
    public ResponseEntity<List<RequestViewDTO>> getSentInquiriesApi(HttpSession session) {
        String requesterCode = (String) session.getAttribute("SCD");
        if (requesterCode == null || requesterCode.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<RequestViewDTO> sentInquiries = outsourcingRequestService.getSentInquiries(requesterCode);
        return ResponseEntity.ok(sentInquiries);
    }

    // 보낸 신청/문의 상세 페이지
    @GetMapping("/{requestId}")
    public String showRequestDetail(@PathVariable String requestId, Model model) {
        RequestViewDTO requestDetail = outsourcingRequestService.getRequestDetails(requestId);
        if (requestDetail == null) {
            return "redirect:/user/outsourcing-requests/sent"; 
        }
        model.addAttribute("request", requestDetail);
        return "user/outsourcingRequest/requestDetailView"; 
    }
}