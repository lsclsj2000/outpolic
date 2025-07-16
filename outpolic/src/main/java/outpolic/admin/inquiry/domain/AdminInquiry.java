package outpolic.admin.inquiry.domain;

import lombok.Data;

@Data
public class AdminInquiry {
	private String inquiryCode;
	private String inquiryTitle;
	private String inquiryCn;
	private String inquiryRegYmdt;
	private String inquiryMdfcnAdmCode;
	private String inquiryMdfcnYmdt;
	
	private String inquiryTypeCode;
	private String inquiryTypeName;
	
	private String memberCode;
	private String memberName;

	private String inquiryProcessCode;
	private String statusCode;
	private String statusName;
	
	private String subAttchCode;
	private String subAttchOrgnlName;
	private String subAttchSrvrName;
	private String subAttchPath;
	private String subAttchSize;
}