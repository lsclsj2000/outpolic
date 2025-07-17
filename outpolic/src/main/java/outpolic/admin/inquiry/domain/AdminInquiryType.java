package outpolic.admin.inquiry.domain;

import lombok.Data;

@Data
public class AdminInquiryType {
	private String inquiryTypeCode;
	private String inquiryTypeName;
	private String inquiryTypeExpln;
	private String statusCode;
	private String statusName;
	private String inquiryTypeRegAdm;
	private String inquiryTypeRegDate;
	private String inquiryTypeMdfcnAdm;
	private String inquiryTypeMdfcnDate;

}