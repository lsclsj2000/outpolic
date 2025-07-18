package outpolic.user.portfolioInquiry.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import outpolic.enter.portfolio.domain.EnterPortfolio;
import outpolic.enter.portfolio.service.EnterPortfolioService;
import outpolic.enter.outsourcingRequest.domain.OutsourcingRequestDTO; // 기존 DTO 재사용
import outpolic.user.portfolioInquiry.service.PortfolioInquiryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 일반 회원(user)의 포트폴리오 문의 기능을 담당하는 컨트롤러
 */
@Controller
@RequestMapping("/user/portfolio-inquiry")
@RequiredArgsConstructor
public class PortfolioInquiryController {

    private final PortfolioInquiryService portfolioInquiryService;
    private final EnterPortfolioService enterPortfolioService; // 포트폴리오 정보 조회를 위해 기존 서비스 재사용

    /**
     * 포트폴리오 문의 작성 폼을 보여주는 메서드
     * @param prtfCd 문의할 포트폴리오의 코드
     * @param model 뷰에 데이터를 전달하기 위한 객체
     * @return 문의 작성 폼 HTML 경로
     */
    @GetMapping("/form/{prtfCd}")
    public String showInquiryForm(@PathVariable String prtfCd, Model model, HttpSession session) {
        // 1. 로그인 상태 확인
        if (session.getAttribute("SCD") == null) {
            return "redirect:/login"; // 로그인 페이지로 리다이렉트
        }

        // 2. 문의 대상 포트폴리오 정보 조회
        EnterPortfolio portfolio = enterPortfolioService.getPortfolioByPrtfCd(prtfCd);
        if (portfolio == null) {
            // 포트폴리오가 존재하지 않을 경우 예외 처리
            return "redirect:/portfolio/list?error=notfound"; // 포트폴리오 목록으로 리다이렉트
        }
        
        model.addAttribute("portfolio", portfolio);
        return "user/portfolioInquiry/inquiryForm";
    }

    /**
     * 작성된 포트폴리오 문의를 서버로 전송하여 저장하는 메서드
     * @param inquiryDto 폼에서 전송된 데이터
     * @param session 현재 사용자 세션
     * @return 성공/실패 정보를 담은 ResponseEntity 객체
     */
    @PostMapping("/send")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> sendInquiry(@ModelAttribute OutsourcingRequestDTO inquiryDto, HttpSession session) {
        String loggedInUserCode = (String) session.getAttribute("SCD");
        if (loggedInUserCode == null || loggedInUserCode.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", "로그인이 필요합니다."));
        }

        // 1. DTO에 필수 정보 설정
        inquiryDto.setMbr_cd(loggedInUserCode); // 문의 보내는 사람 (로그인한 일반 회원)
        
        // 2. ★★★ 핵심: 요청 타입을 '문의'로 명시적으로 설정
        inquiryDto.setOcd_req_type("문의");

        // 3. 서비스 호출하여 문의 생성
        portfolioInquiryService.createInquiry(inquiryDto);

        // 4. 성공 응답 반환
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "포트폴리오 문의가 성공적으로 전송되었습니다.",
            "redirectUrl", "/user/outsourcing-requests/sent" // 문의/신청을 함께 볼 수 있는 '보낸 요청' 목록 페이지로 이동
        ));
    }
}