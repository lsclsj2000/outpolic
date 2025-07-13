package outpolic.user.outsourcingRequest.controller;

import lombok.RequiredArgsConstructor;
import outpolic.enter.outsourcing.domain.EnterOutsourcing; // 기존 도메인 재사용
import outpolic.enter.outsourcing.service.EnterOutsourcingService; // 기존 서비스 재사용 (외주 상세 정보 조회용)
import outpolic.enter.outsourcingRequest.domain.OutsourcingRequestDTO; // 기존 DTO 재사용
import outpolic.enter.outsourcingRequest.domain.RequestViewDTO; // 기존 DTO 재사용
import outpolic.user.outsourcingRequest.service.OutsourcingRequestService; // User용 서비스 사용

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

@Controller("userOutsourcingRequestController")
@RequestMapping("/user/outsourcing-requests") // 사용자용 기본 URL 경로
@RequiredArgsConstructor
public class OutsourcingRequestController {

    private final OutsourcingRequestService outsourcingRequestService; // User용 서비스
    private final EnterOutsourcingService enterOutsourcingService; // 외주 상세 정보를 가져오는 기존 서비스

    // 외주 신청 폼 페이지
    @GetMapping("/form/{osCd}")
    public String showRequestForm(@PathVariable String osCd, Model model) {
        EnterOutsourcing outsourcing = enterOutsourcingService.findOutsourcingDetailsByOsCd(osCd); 
        if (outsourcing == null) {
            return "redirect:/user/outsourcing/list"; // 해당 외주가 없으면 목록으로 리다이렉트
        }
        model.addAttribute("outsourcing", outsourcing); 
        return "user/outsourcingRequest/requestForm"; 
    }

    // 외주 신청 폼 제출 처리
    @PostMapping
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createRequest(
            @ModelAttribute OutsourcingRequestDTO requestDto,
            HttpSession session) {
        
        String loggedInUserCode = (String) session.getAttribute("SCD"); // 사용자(일반 회원)의 회원 코드

        if (loggedInUserCode == null || loggedInUserCode.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", "로그인이 필요합니다.")); 
        }
        
        requestDto.setMbr_cd(loggedInUserCode); // 수요자(사용자)의 회원 코드 설정
        
        outsourcingRequestService.createRequest(requestDto); 
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "외주 신청이 완료되었습니다."
        )); 
    }

    // 보낸 외주 신청 목록 페이지
    @GetMapping("/sent")
    public String showSentRequests(Model model, HttpSession session) {
        String requesterCode = (String) session.getAttribute("SCD"); // 사용자(일반 회원)의 회원 코드

        if (requesterCode == null || requesterCode.isEmpty()) {
            return "redirect:/login"; 
        }
        List<RequestViewDTO> sentRequests = outsourcingRequestService.getSentRequests(requesterCode); 
        model.addAttribute("requests", sentRequests); 
        return "user/outsourcingRequest/sentRequestsList"; 
    }

    // (수정) 보낸 외주 신청 목록 데이터를 JSON 형태로 반환하는 API 엔드포인트
    @GetMapping("/api/sent") // 템플릿의 fetch 요청 경로와 일치시킴
    @ResponseBody
    public ResponseEntity<List<RequestViewDTO>> getSentRequestsApi(HttpSession session) {
        String requesterCode = (String) session.getAttribute("SCD"); // 사용자(일반 회원)의 회원 코드

        if (requesterCode == null || requesterCode.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); 
        }
        List<RequestViewDTO> sentRequests = outsourcingRequestService.getSentRequests(requesterCode); 
        return ResponseEntity.ok(sentRequests); 
    }

    // 보낸 외주 신청 상세 페이지
    @GetMapping("/{requestId}")
    public String showRequestDetail(@PathVariable String requestId, Model model) {
        RequestViewDTO requestDetail = outsourcingRequestService.getRequestDetails(requestId); 
        if (requestDetail == null) {
            return "redirect:/user/outsourcing-requests/sent"; // 요청이 없으면 목록으로 리다이렉트
        }
        model.addAttribute("request", requestDetail); 
        return "user/outsourcingRequest/requestDetailView"; 
    }
}