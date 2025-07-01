package outpolic.user.payment.dto;

import lombok.Data;

@Data
public class CardDTO {
	private String issuerCode;				// 카드 발습사 두 자리 코드
	private String acquirerCode;			// 카드 매입사 두 자리 코드
	private String number;					// 카드 번호 (일부 마스킹)
	private String installmentPlanMonths;	// 할부 개월 수 (일시불은 0)
	private String isInterestFree;			// 무이자 할부 적용 여부 (할부 안함 = 필요없는 데이터)
	private String interestPayer;			// 할부가 적용된 결제에서 할부 수수료를 부담하는 주체
	private String approveNo;				// 카드사 승인 번호
	private String useCardPoint;			// 카드사 포인트 사용 여부
	private String cardType;				// 카드 종류 (신용, 체크, 기프트, 미확인)
	private String ownerType;				// 카드의 소유자 타입 (개인, 법인, 미확인)
	private String acquireStatus;			// 카드 결제의 매입 상태
	private String amount;					// 카드사에 결제 요청한 금액
}
