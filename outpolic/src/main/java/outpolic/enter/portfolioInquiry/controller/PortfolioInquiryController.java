package outpolic.enter.portfolioInquiry.controller;

import lombok.RequiredArgsConstructor;
import outpolic.enter.portfolio.domain.EnterPortfolio;
import outpolic.enter.portfolio.service.EnterPortfolioService;
import outpolic.enter.outsourcingRequest.domain.OutsourcingRequest;
import outpolic.enter.outsourcingRequest.service.OutsourcingRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/enter/portfolio-inquiry") // 포트폴리오 문의 전용 URL
@RequiredArgsConstructor
public class PortfolioInquiryController {

    private final EnterPortfolioService portfolioService;
    private final OutsourcingRequestService requestService;

    /**
     * 포트폴리오 기반 문의 작성 페이지(View)를 보여주는 메서드
     * @param prtfCd 문의의 기반이 되는 포트폴리오 코드
     * @param model View에 데이터를 전달하기 위한 객체
     * @return 문의 작성 페이지의 경로
     */
    @GetMapping("/{prtfCd}")
    public String showInquiryForm(@PathVariable String prtfCd, Model model) {
        // 1. 문의할 포트폴리오의 상세 정보를 조회합니다.
        EnterPortfolio portfolio = portfolioService.getPortfolioByPrtfCd(prtfCd);

        // 2. View에 포트폴리오 객체와 공급자 코드를 전달합니다.
        model.addAttribute("portfolio", portfolio);
        model.addAttribute("supplierCd", portfolio.getEntCd());

        // 3. 문의 작성 페이지 View를 반환합니다.
        return "enter/portfolioInquiry/portfolioInquiryListView"; // 포트폴리오 문의 전용 View 경로
    }

    /**
     * 작성된 포트폴리오 문의를 접수(저장)하는 메서드
     * @param request 폼에서 전송된 데이터가 담길 DTO 객체
     * @return 처리 결과 (성공/실패)
     */
    @PostMapping("/submit")
    @ResponseBody
    public ResponseEntity<?> submitInquiry(@ModelAttribute OutsourcingRequest request) {
        try {
            // TODO: 실제 로그인한 사용자의 회원 코드를 세션에서 가져와 설정해야 합니다.
            // String loginMemberCd = (String) session.getAttribute("loginMemberCd");
            // request.setMbrCd(loginMemberCd);
            request.setMbrCd("MB_C0000007"); // 임시 회원 코드 사용

            // 포트폴리오 문의임을 명시적으로 설정 (요청 타입 등)
            request.setOcdReqType("문의");

            requestService.addOutsourcingRequest(request);
            return ResponseEntity.ok(Map.of("success", true, "message", "포트폴리오 문의가 성공적으로 접수되었습니다."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "문의 처리 중 오류가 발생했습니다."));
        }
    }
}