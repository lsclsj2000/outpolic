package outpolic.enter.inquiry.domain;

import lombok.Data;

@Data
public class EnterInquiryProcess {
	private String iqpCode;
	private String iqCode;
	private String iqpAnsCn;
	private String iqpRegYmdt;
	private String statusName;
	private String memberName;
}