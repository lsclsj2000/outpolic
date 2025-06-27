package outpolic.user.inquiry.domain;

import lombok.Data;

@Data
public class UserInquiry {
	private String inquiryCode;
	private String memberName;
	private String inquiryTypeName;
	private String inquiryTitle;
	private String inquiryCn;
	private String inquiryRegDate;
	private String subAttOrgnlName;
	private String subAttSrvrName;
	private String subAttPath;
	private String inquiryProcessCn;
	private String inquiryProcessRegDate;
	private String adminCode;
	private String adminName;
	private String inquiryTypeCode;
	private String memberCode;
	
	private String inquiryTypeExpln; 
	private String statusCode;
	private String inquiryTypeRegAdm; 
	private String inquiryTypeRegDate;
	private String inquiryTypeMdfcnAdm; 
	private String inquiryTypeMdfcnDate;
	private String inquiryProcessAnsCn;
}