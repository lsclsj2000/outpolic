package outpolic.admin.inquiry.domain;

import lombok.Data;

@Data
public class AdminInquiry {
	private String inquiryCode;
	private String memberName;
	private String inquiryTypeCode;
	private String inquiryTypeName;
	private String inquiryTitle;
	private String inquiryRegYmdt;
	private String inquiryProcessCode;
	private String statusCode;
	private String statusName;
}