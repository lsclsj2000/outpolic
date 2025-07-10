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
    private PaymentMapper Paymentmapper;

    // 일반회원 상품결제
    @GetMapping("/user/paymentSuccess")
    public String userPaymentSuccessPage(String orderId, String paymentKey, String amount, HttpSession session, String grdCd, Integer usedMileage) {
        log.info("결제 성공 콜백 수신");
        log.info("orderId: {}", orderId);
        log.info("paymentKey: {}", paymentKey);
        log.info("amount: {}", amount);
        log.info("grdCd: {}", grdCd);
        log.info("usedMileage: {}", usedMileage);

        
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
            PaymentDTO userPayment = objectMapper.readValue(response.body(), PaymentDTO.class);
            log.info("결제 응답 DTO: {}", userPayment);

           

            
            
            // 3. SettlementDTO 생성 및 매핑
            SettlementDTO settlement = new SettlementDTO();
            settlement.setMbrCd("MB_C0000035"); // 세션 또는 로그인 정보에서 가져오는게 좋음
            settlement.setGdsCd(grdCd); // 프론트에서 상품 선택 정보를 받아오는게 바람직함
            settlement.setStcCd("SD_SUCCESS"); // 상태코드 테이블에 정의된 값
            settlement.setStlmCnt(1); // 예시: 1개 구매
            settlement.setStlmPayType("카드"); // 또는 userPayment.getCard().getCardType()
            settlement.setStlmAmt(new BigDecimal(amount)); // 원래 금액
            settlement.setStlmUsedPoints(usedMileage); // 마일리지 사용 안함
            settlement.setStlmFinalAmt(new BigDecimal(amount)); // 최종 금액
            CardDTO card = userPayment.getCard();
            EasyPayDTO easy = userPayment.getEasyPay();
            if (card != null) {
            	String cardCompanyName = getCardAcquirerName(card.getIssuerCode());
                settlement.setStlmPaymentInfo(card.getNumber());
                settlement.setStlmCardNm(cardCompanyName);
                settlement.setStlmAccountNm("홍길동");	// 프론트에서 결제자 이름을 받아와야함
            } else {
                settlement.setStlmPaymentInfo("간편결제");
                settlement.setStlmCardNm("N/A");
                settlement.setStlmAccountNm("홍길동");
            }
            settlement.setStlmProviderNm(easy.getProvider());
            settlement.setStlmAccountInfo("N/A");
            settlement.setStlmYmdt(Timestamp.valueOf(OffsetDateTime.parse(userPayment.getApprovedAt()).toLocalDateTime()));
            settlement.setStlmAgreYn(true);

            // 4. DB 저장
            int result = Paymentmapper.insertSettlement(settlement);
            log.info("settlement 저장 결과: {}", result);

        } catch (IOException | InterruptedException e) {
            log.error("결제 승인 처리 중 오류", e);
        }
        
        
        
        // 알림창 표시용 (리다이렉트 후 알림 띄우는 처리 가능)
        return "redirect:/userGoodsList"; // 나중에 JS alert 띄우는 방식으로 바꿀 수 있음
    }
    
    // 기업회원 상품결제
    @GetMapping("/enter/paymentSuccess")
    public String enterPaymentSuccessPage(String orderId, String paymentKey, String amount, HttpSession session, String grdCd, Integer usedMileage) {
    	log.info("결제 성공 콜백 수신");
    	log.info("orderId: {}", orderId);
    	log.info("paymentKey: {}", paymentKey);
    	log.info("amount: {}", amount);
    	log.info("grdCd: {}", grdCd);
    	log.info("usedMileage: {}", usedMileage);
    	
    	
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
    		PaymentDTO userPayment = objectMapper.readValue(response.body(), PaymentDTO.class);
    		log.info("결제 응답 DTO: {}", userPayment);
    		
    		
    		
    		
    		
    		// 3. SettlementDTO 생성 및 매핑
    		SettlementDTO settlement = new SettlementDTO();
    		settlement.setMbrCd("MB_C0000036"); // 세션 또는 로그인 정보에서 가져오는게 좋음
    		settlement.setGdsCd(grdCd); // 프론트에서 상품 선택 정보를 받아오는게 바람직함
    		settlement.setStcCd("SD_SUCCESS"); // 상태코드 테이블에 정의된 값
    		settlement.setStlmCnt(1); // 예시: 1개 구매
    		settlement.setStlmPayType("카드"); // 또는 userPayment.getCard().getCardType()
    		settlement.setStlmAmt(new BigDecimal(amount)); // 원래 금액
    		settlement.setStlmUsedPoints(usedMileage); // 마일리지 사용 안함
    		settlement.setStlmFinalAmt(new BigDecimal(amount)); // 최종 금액
    		CardDTO card = userPayment.getCard();
    		EasyPayDTO easy = userPayment.getEasyPay();
    		if (card != null) {
    			String cardCompanyName = getCardAcquirerName(card.getIssuerCode());
    			settlement.setStlmPaymentInfo(card.getNumber());
    			settlement.setStlmCardNm(cardCompanyName);
    			settlement.setStlmAccountNm("홍길동");	// 프론트에서 결제자 이름을 받아와야함
    		} else {
    			settlement.setStlmPaymentInfo("간편결제");
    			settlement.setStlmCardNm("N/A");
    			settlement.setStlmAccountNm("홍길동");
    		}
    		settlement.setStlmProviderNm(easy.getProvider());
    		settlement.setStlmAccountInfo("N/A");
    		settlement.setStlmYmdt(Timestamp.valueOf(OffsetDateTime.parse(userPayment.getApprovedAt()).toLocalDateTime()));
    		settlement.setStlmAgreYn(true);
    		
    		// 4. DB 저장
    		int result = Paymentmapper.insertSettlement(settlement);
    		log.info("settlement 저장 결과: {}", result);
    		
    	} catch (IOException | InterruptedException e) {
    		log.error("결제 승인 처리 중 오류", e);
    	}
    	
    	
    	
    	// 알림창 표시용 (리다이렉트 후 알림 띄우는 처리 가능)
    	return "redirect:/enterGoodsList"; // 나중에 JS alert 띄우는 방식으로 바꿀 수 있음
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
