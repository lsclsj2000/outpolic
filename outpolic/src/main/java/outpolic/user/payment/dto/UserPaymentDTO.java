package outpolic.user.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


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
	
	public String getmId() {
		return mId;
	}
	public void setmId(String mId) {
		this.mId = mId;
	}
	public String getLastTransactionKey() {
		return lastTransactionKey;
	}
	public void setLastTransactionKey(String lastTransactionKey) {
		this.lastTransactionKey = lastTransactionKey;
	}
	public String getPaymentKey() {
		return paymentKey;
	}
	public void setPaymentKey(String paymentKey) {
		this.paymentKey = paymentKey;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getOrderName() {
		return orderName;
	}
	public void setOrderName(String orderName) {
		this.orderName = orderName;
	}
	public String getTaxExemptionAmount() {
		return taxExemptionAmount;
	}
	public void setTaxExemptionAmount(String taxExemptionAmount) {
		this.taxExemptionAmount = taxExemptionAmount;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRequestedAt() {
		return requestedAt;
	}
	public void setRequestedAt(String requestedAt) {
		this.requestedAt = requestedAt;
	}
	public String getApprovedAt() {
		return approvedAt;
	}
	public void setApprovedAt(String approvedAt) {
		this.approvedAt = approvedAt;
	}
	public String getUseEscrow() {
		return useEscrow;
	}
	public void setUseEscrow(String useEscrow) {
		this.useEscrow = useEscrow;
	}
	public String getCultureExpense() {
		return cultureExpense;
	}
	public void setCultureExpense(String cultureExpense) {
		this.cultureExpense = cultureExpense;
	}
	public CardDTO getCard() {
		return card;
	}
	public void setCard(CardDTO card) {
		this.card = card;
	}
	public EasyPayDTO getEasyPay() {
		return easyPay;
	}
	public void setEasyPay(EasyPayDTO easyPay) {
		this.easyPay = easyPay;
	}
	@Override
	public String toString() {
		return "UserPaymentDTO [mId=" + mId + ", lastTransactionKey=" + lastTransactionKey + ", paymentKey="
				+ paymentKey + ", orderId=" + orderId + ", orderName=" + orderName + ", taxExemptionAmount="
				+ taxExemptionAmount + ", status=" + status + ", requestedAt=" + requestedAt + ", approvedAt="
				+ approvedAt + ", useEscrow=" + useEscrow + ", cultureExpense=" + cultureExpense + ", card=" + card
				+ ", easyPay=" + easyPay + "]";
	}
	
	
}
