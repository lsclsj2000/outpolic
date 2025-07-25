package outpolic.systems.payment.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Timestamp;
import java.time.OffsetDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import outpolic.systems.payment.dto.CardDTO;
import outpolic.systems.payment.dto.EasyPayDTO;
import outpolic.systems.payment.dto.PaymentDTO;
import outpolic.systems.payment.dto.SettlementDTO;
import outpolic.systems.payment.mapper.PaymentMapper;

@Controller
@Slf4j
public class PaymentController {

	// 결제 카드사 매핑
	private String getCardAcquirerName(String acquirerCode) {
		return switch (acquirerCode) {
			case "3K" -> "기업비씨";
			case "46" -> "광주";
			case "71" -> "롯데";
			case "30" -> "산업";
			case "51" -> "삼성";
			case "38" -> "새마을";
			case "41" -> "신한";
			case "62" -> "신협";
			case "36" -> "씨티";
			case "33" -> "우리";
			case "W1" -> "우리";
			case "37" -> "우체국";
			case "39" -> "저축";
			case "35" -> "전북";
			case "42" -> "제주";
			case "15" -> "카카오뱅크";
			case "3A" -> "케이뱅크";
			case "24" -> "토스뱅크";
			case "21" -> "하나";
			case "61" -> "현대";
			case "11" -> "국민";
			case "91" -> "농협";
			case "34" -> "수협";
			default -> "기타카드";
		};
	}

	@Autowired
	private PaymentMapper paymentMapper; // [수정] 변수명 컨벤션 수정 (소문자 시작)

	/**
	 * [리팩토링] 일반/기업회원 결제 성공 로직을 공통 메소드로 추출하여 중복 제거
	 */
	private void processPayment(String orderId, String paymentKey, String amount, HttpSession session,
	                            String grdCd, Integer usedMileage, Integer orderCount, Integer originAmount) throws IOException, InterruptedException {
	    
	    // 1. TossPayments API에 결제 승인 요청
	    HttpRequest request = HttpRequest.newBuilder()
	            .uri(URI.create("https://api.tosspayments.com/v1/payments/confirm"))
	            .header("Authorization", "Basic dGVzdF9za19qRXhQZUpXWVZRUkE5UWFnSmRwb3I0OVI1Z3ZOOg==") // 테스트 시크릿 키
	            .header("Content-Type", "application/json")
	            .method("POST", HttpRequest.BodyPublishers.ofString(String.format(
	                    "{\"paymentKey\":\"%s\",\"orderId\":\"%s\",\"amount\":%s}", paymentKey, orderId, amount)))
	            .build();
	    HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

	    // 2. 응답 결과를 PaymentDTO로 매핑
	    ObjectMapper objectMapper = new ObjectMapper();
	    PaymentDTO tossPaymentResponse = objectMapper.readValue(response.body(), PaymentDTO.class);
	    log.info("결제 응답 DTO: {}", tossPaymentResponse);

	    String memberCode = (String) session.getAttribute("SCD");

	    // 3. SettlementDTO 생성 및 최종 매핑
	    SettlementDTO settlement = new SettlementDTO();
	    settlement.setMbrCd(memberCode);
	    settlement.setGdsCd(grdCd);
	    settlement.setStcCd("SD_SUCCESS");
	    settlement.setStlmCnt(orderCount);
	    settlement.setStlmAmt(new BigDecimal(originAmount));
	    settlement.setStlmUsedPoints(usedMileage);
	    settlement.setStlmFinalAmt(new BigDecimal(amount));
	    settlement.setStlmYmdt(
	            Timestamp.valueOf(OffsetDateTime.parse(tossPaymentResponse.getApprovedAt()).toLocalDateTime()));

	    // [핵심 수정] method 필드를 기준으로 카드/간편결제 명확히 구분
	    String paymentMethod = tossPaymentResponse.getMethod();
	    settlement.setStlmMethod(paymentMethod);

	    CardDTO card = tossPaymentResponse.getCard();
	    EasyPayDTO easy = tossPaymentResponse.getEasyPay();

	    if (card != null) {
	        // 카드 정보가 있으면 무조건 카드 정보를 저장
	        settlement.setStlmPaymentInfo(card.getNumber());
	        settlement.setStlmCardNm(getCardAcquirerName(card.getIssuerCode()));
	        settlement.setStlmCardType(card.getCardType());
	    } else {
	        // 카드 정보가 없을 때만 '간편결제'로 저장
	        settlement.setStlmPaymentInfo("간편결제");
	        settlement.setStlmCardNm("N/A");
	        settlement.setStlmCardType("N/A");
	    }
	    // 간편결제사 정보는 easyPay 객체가 있을 때만 저장
	    if (easy != null) {
	        settlement.setStlmProviderNm(easy.getProvider());
	    } else {
	        settlement.setStlmProviderNm("N/A");
	    }
	    // 4. DB 저장
	    int result = paymentMapper.insertSettlement(settlement);
	    log.info("settlement 저장 결과: {}", result);
	}

	// 일반회원 상품결제
	@GetMapping("/user/paymentSuccess")
	public String userPaymentSuccessPage(String orderId, String paymentKey, String amount, HttpSession session,
			String grdCd, Integer usedMileage, Integer orderCount, Integer originAmount) {
		try {
			processPayment(orderId, paymentKey, amount, session, grdCd, usedMileage, orderCount, originAmount);
		} catch (IOException | InterruptedException e) {
			log.error("결제 승인 처리 중 오류", e);
			return "redirect:/user/paymentFail";
		}
		return "redirect:/userGoodsList";
	}

	// 기업회원 상품결제
	@GetMapping("/enter/paymentSuccess")
	public String enterPaymentSuccessPage(String orderId, String paymentKey, String amount, HttpSession session,
			String grdCd, Integer usedMileage, Integer orderCount, Integer originAmount) {
		try {
			processPayment(orderId, paymentKey, amount, session, grdCd, usedMileage, orderCount, originAmount);
		} catch (IOException | InterruptedException e) {
			log.error("결제 승인 처리 중 오류", e);
			return "redirect:/enter/paymentFail";
		}
		return "redirect:/enterGoodsList";
	}

	@GetMapping("/user/paymentFail")
	public String userPaymentFailPage() {
		return "user/goods/paymentFail";
	}

	@GetMapping("/enter/paymentFail")
	public String enterPaymentFailPage() {
		return "enter/goods/paymentFail";
	}
}