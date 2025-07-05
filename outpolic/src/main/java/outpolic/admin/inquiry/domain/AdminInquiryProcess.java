package outpolic.admin.inquiry.domain;

import lombok.Data;

@Data
public class AdminInquiryProcess {
	private String iqpCode;
	private String iqCode;
	private String iqpAnsCn;
	private String iqpRegYmdt;
	private String statusName;
	private String memberName;
}