package outpolic.enter.outsourcingRequest.controller;

import lombok.RequiredArgsConstructor;
import outpolic.enter.outsourcing.domain.EnterOutsourcing;
import outpolic.enter.outsourcing.service.EnterOutsourcingService;
import outpolic.enter.outsourcingRequest.domain.OutsourcingRequestDTO;
import outpolic.enter.outsourcingRequest.domain.RequestViewDTO;
import outpolic.enter.outsourcingRequest.service.OutsourcingRequestService;

// import org.springframework.security.core.annotation.AuthenticationPrincipal; // ★ 삭제
// import org.springframework.security.core.userdetails.UserDetails; // ★ 삭제
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession; // ★ 추가: HttpSession 임포트

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
            HttpSession session // ★ 수정: @AuthenticationPrincipal UserDetails userDetails 대신 HttpSession 사용
            ) {
        // ★ 수정: userDetails == null 체크 대신 세션에서 사용자 ID 가져오기
        String loggedInUserId = (String) session.getAttribute("SMbrCd"); // 로그인 시 세션에 저장한 사용자 ID 키 "SID" 사용 [cite: 1]

        if (loggedInUserId == null || loggedInUserId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", "로그인이 필요합니다."));
        }
        
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
    public String showSentRequests(Model model, HttpSession session) { // ★ 수정: @AuthenticationPrincipal UserDetails userDetails 대신 HttpSession 사용
        // ★ 수정: userDetails == null 체크 대신 세션에서 사용자 ID 가져오기
        String requesterId = (String) session.getAttribute("SID"); // 로그인 시 세션에 저장한 사용자 ID 키 "SID" 사용 [cite: 1]

        if (requesterId == null || requesterId.isEmpty()) {
            return "redirect:/login";
        }
        
        List<RequestViewDTO> sentRequests = requestService.getSentRequests(requesterId);
        
        model.addAttribute("requests", sentRequests);
        
        // "내가 보낸 신청 목록"을 보여줄 새로운 view 파일 경로
        return "enter/outsourcingRequest/sentRequestsList";
    }
    
    
    @GetMapping("/check-my-role")
    @ResponseBody
    public String checkMyRole(HttpSession session) { // ★ 수정: @AuthenticationPrincipal UserDetails userDetails 대신 HttpSession 사용
        // ★ 수정: userDetails == null 체크 대신 세션에서 사용자 ID 가져오기
        String username = (String) session.getAttribute("SID"); // 로그인 시 세션에 저장한 사용자 ID 키 "SID" 사용 [cite: 1]
        String grade = (String) session.getAttribute("SGrd"); // 로그인 시 세션에 저장한 등급 키 "SGrd" 사용 [cite: 1]

        if (username == null || username.isEmpty()) {
            return "<h2>로그인되지 않은 상태입니다.</h2>";
        }
        
        // 권한 정보는 UserLoginController에서 SGrd로 세션에 저장했으므로, 이를 활용합니다.
        // Spring Security의 GrantedAuthority 객체는 아니지만, 세션에 저장된 등급 정보를 활용합니다.
        String authoritiesInfo = (grade != null) ? "ROLE_" + grade : "권한 없음"; // 예시: 등급에 따라 ROLE_ENTER, ROLE_USER 등으로 표시

        return "<h1>로그인 정보 확인</h1>" +
               "<h2>로그인 ID: " + username + "</h2>" +
               "<h2>현재 부여된 권한 (세션 기준): " + authoritiesInfo + "</h2>" +
               "<p>이 페이지가 보인다면, 로그인은 성공했으며 /enter/ 경로에 접근은 가능하다는 의미입니다.</p>" +
               "<p>만약 '현재 부여된 권한'에 'ROLE_USER' 또는 'ROLE_ENTER'가 없다면, 그것이 바로 문제입니다.</p>";
    }
}