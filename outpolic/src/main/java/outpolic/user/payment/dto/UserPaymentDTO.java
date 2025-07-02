package outpolic.user.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown =true)
public class UserPaymentDTO {
	private String mId;					// 상점번호
	private String lastTransactionKey;	// 마지막 거래 키
	private String paymentKey;			// 결제 키
	private String orderId;				// 주문번호
	private String orderName;			// 구매상품
	private String taxExemptionAmount;	// 과세 제외한 금액	필요없어보임
	private String status;				// 결제 처리상태 
	private String requestedAt;			// 결제 일시
	private String approvedAt;			// 결제 승인 일시
	private String useEscrow;			// 		필요없어보임
	private String cultureExpense;		// 문화비 필요없어보임
	private CardDTO card;				// 카드로 결제하면 제공되는 카드 관련 정보
	private EasyPayDTO easyPay;			// 간편결제로 결제하면 제공되는 정보
	
	
	
}
