package outpolic.user.payment.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import outpolic.user.payment.dto.CardDTO;
import outpolic.user.payment.dto.EasyPayDTO;
import outpolic.user.payment.dto.SettlementDTO;
import outpolic.user.payment.dto.UserPaymentDTO;
import outpolic.user.payment.mapper.UserPaymentMapper;

@Controller
@Slf4j
public class UserPaymentController {

	private String getCardAcquirerName(String acquirerCode) {
	    return switch (acquirerCode) {
	        case "11" -> "국민카드";
	        case "12" -> "하나카드";
	        case "13" -> "외환카드";
	        case "14" -> "삼성카드";
	        case "15" -> "신한카드";
	        case "16" -> "롯데카드";
	        case "17" -> "현대카드";
	        case "18" -> "BC카드";
	        case "19" -> "NH농협카드";
	        case "21" -> "우리카드";
	        case "22" -> "카카오뱅크카드";
	        default -> "기타카드";
	    };
	}
	
    @Autowired
    private UserPaymentMapper userPaymentmapper;

    @GetMapping("/paymentSuccess")
    public String paymentSuccessPage(String orderId, String paymentKey, String amount, HttpSession session) {
        log.info("✅ 결제 성공 콜백 수신");
        log.info("orderId: {}", orderId);
        log.info("paymentKey: {}", paymentKey);
        log.info("amount: {}", amount);

        try {
            // 1. TossPayments API에 결제 승인 요청
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.tosspayments.com/v1/payments/confirm"))
                    .header("Authorization", "Basic dGVzdF9za19qRXhQZUpXWVZRUkE5UWFnSmRwb3I0OVI1Z3ZOOg==") // 테스트 시크릿 키
                    .header("Content-Type", "application/json")
                    .method("POST", HttpRequest.BodyPublishers.ofString(
                            String.format("{\"paymentKey\":\"%s\",\"orderId\":\"%s\",\"amount\":%s}",
                                    paymentKey, orderId, amount)))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            // 2. 응답 결과를 UserPaymentDTO로 매핑
            ObjectMapper objectMapper = new ObjectMapper();
            UserPaymentDTO userPayment = objectMapper.readValue(response.body(), UserPaymentDTO.class);
            log.info("결제 응답 DTO: {}", userPayment);

           

            
            
            // 3. SettlementDTO 생성 및 매핑
            SettlementDTO settlement = new SettlementDTO();
            settlement.setStlmCd("STLM_C2"); // 결제코드 생성
            settlement.setMbrCd("MB_C0000035"); // 세션 또는 로그인 정보에서 가져오는게 좋음
            settlement.setGdsCd("PD_C002"); // 프론트에서 상품 선택 정보를 받아오는게 바람직함
            settlement.setStcCd("SD_SUCCESS"); // 상태코드 테이블에 정의된 값
            settlement.setStlmCnt(1); // 예시: 1개 구매
            settlement.setStlmPayType("카드"); // 또는 userPayment.getCard().getCardType()
            settlement.setStlmAmt(new BigDecimal(amount)); // 원래 금액
            settlement.setStlmUsedPoints(BigDecimal.ZERO); // 마일리지 사용 안함
            settlement.setStlmFinalAmt(new BigDecimal(amount)); // 최종 금액
            CardDTO card = userPayment.getCard();
            EasyPayDTO easy = userPayment.getEasyPay();
            if (card != null) {
            	String cardCompanyName = getCardAcquirerName(card.getIssuerCode());
                settlement.setStlmPaymentInfo(card.getNumber());
                settlement.setStlmCardNm(cardCompanyName);
                settlement.setStlmAccountNm(userPayment.getOrderName());
            } else {
                settlement.setStlmPaymentInfo("간편결제");
                settlement.setStlmCardNm("N/A");
                settlement.setStlmAccountNm(userPayment.getOrderName());
            }
            settlement.setStlmProviderNm(easy.getProvider());
            settlement.setStlmAccountInfo("N/A");
            settlement.setStlmYmdt(Timestamp.valueOf(OffsetDateTime.parse(userPayment.getApprovedAt()).toLocalDateTime()));
            settlement.setStlmAgreYn(true);

            // 4. DB 저장
            int result = userPaymentmapper.insertSettlement(settlement);
            log.info("settlement 저장 결과: {}", result);

        } catch (IOException | InterruptedException e) {
            log.error("결제 승인 처리 중 오류", e);
        }

        // 알림창 표시용 (리다이렉트 후 알림 띄우는 처리 가능)
        return "user/goods/paymentSuccess"; // 나중에 JS alert 띄우는 방식으로 바꿀 수 있음
    }

    @GetMapping("/paymentFail")
    public String paymentFailPage() {
        return "user/goods/paymentFail";
    }
}
