package outpolic.enter.outsourcingRequest.controller;

import lombok.RequiredArgsConstructor;
import outpolic.enter.outsourcing.domain.EnterOutsourcing;
import outpolic.enter.outsourcing.service.EnterOutsourcingService;
import outpolic.enter.outsourcingRequest.domain.OutsourcingRequestDTO;
import outpolic.enter.outsourcingRequest.domain.RequestViewDTO;
import outpolic.enter.outsourcingRequest.service.OutsourcingRequestService;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/enter/outsourcing-requests")
@RequiredArgsConstructor
public class OutsourcingRequestController {

    private final OutsourcingRequestService requestService;
    private final EnterOutsourcingService enterOutsourcingService;

    @GetMapping("/form/{osCd}")
    public String showRequestForm(@PathVariable String osCd, Model model) {
        
     
        
        EnterOutsourcing outsourcing = enterOutsourcingService.getOutsourcingByOsCd(osCd);
        
        // ★수정: demander 객체를 모델에 추가하는 코드는 불필요하므로 완전히 제거되었습니다.
        model.addAttribute("outsourcing", outsourcing);
        
        return "enter/outsourcingRequest/requestForm";
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createRequest(
            @ModelAttribute OutsourcingRequestDTO requestDto,
            @AuthenticationPrincipal UserDetails userDetails
            ) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", "로그인이 필요합니다."));
        }
        
        String loggedInUserId = userDetails.getUsername();
        requestDto.setMbr_cd(loggedInUserId);

        requestService.createRequest(requestDto);
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "외주 신청이 완료되었습니다."
        ));
    }
    
    /**
     * ★추가: 내가 보낸 신청 목록 페이지를 보여주는 메서드
     */
    @GetMapping("/sent")
    public String showSentRequests(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        
        String requesterId = userDetails.getUsername();
        List<RequestViewDTO> sentRequests = requestService.getSentRequests(requesterId);
        
        model.addAttribute("requests", sentRequests);
        
        // "내가 보낸 신청 목록"을 보여줄 새로운 view 파일 경로
        return "enter/outsourcingRequest/sentRequestsList"; 
    }
    
    
    @GetMapping("/check-my-role")
    @ResponseBody
    public String checkMyRole(@AuthenticationPrincipal UserDetails userDetails) {
        
        if (userDetails == null) {
            return "<h2>로그인되지 않은 상태입니다.</h2>";
        }
        
        String username = userDetails.getUsername();
        java.util.Collection<? extends org.springframework.security.core.GrantedAuthority> authorities = userDetails.getAuthorities();
        
        return "<h1>로그인 정보 확인</h1>" +
               "<h2>로그인 ID: " + username + "</h2>" +
               "<h2>현재 부여된 권한: " + authorities + "</h2>" +
               "<p>이 페이지가 보인다면, 로그인은 성공했으며 /enter/ 경로에 접근은 가능하다는 의미입니다.</p>" +
               "<p>만약 '현재 부여된 권한'에 'ROLE_USER' 또는 'ROLE_ENTER'가 없다면, 그것이 바로 문제입니다.</p>";
    }
}