package outpolic.user.inquiry.domain;

import lombok.Data;

@Data
public class UserInquiryProcess {
	private String iqpCode;
	private String iqCode;
	private String iqpAnsCn;
	private String iqpRegYmdt;
	private String statusName;
	private String memberName;
}