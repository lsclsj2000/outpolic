package outpolic.systems.refund.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.systems.refund.service.RefundService;

@Controller
@RequiredArgsConstructor
@Slf4j
public class RefundController {

	private final RefundService refundService;
	
	@PostMapping("/api/requestRefund")
    @ResponseBody
    public ResponseEntity<String> handleRefundRequest(@RequestBody Map<String, String> payload, HttpSession session) {
        String stlmCd = payload.get("stlmCd");
        String mbrCd = (String) session.getAttribute("SCD");

        if (mbrCd == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        try {
            String resultMessage = refundService.processRefundRequest(stlmCd, mbrCd);
            return ResponseEntity.ok(resultMessage);
        } catch (IllegalArgumentException e) {
            // 조건에 맞지 않아 환불이 거부된 경우
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("환불 처리 중 서버 오류", e);
            return ResponseEntity.internalServerError().body("환불 처리 중 오류가 발생했습니다. 관리자에게 문의하세요.");
        }
    }
}
