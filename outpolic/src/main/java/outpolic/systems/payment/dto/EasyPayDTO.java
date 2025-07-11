
package outpolic.systems.payment.dto;


import lombok.Data;

@Data
public class EasyPayDTO {

	private String provider;		// 간편결제사 코드
	private String amount;			// 간편결제 서비스에 드록된 계좌, 포인트로 결제한 금액
	private String discountAmount;	// 간편결제 서비스의 적립 포인트, 쿠폰 등으로 즉시 할인된 금액
}
