package outpolic.admin.inquiry.domain;

import lombok.Data;

@Data
public class AdminInquiryStatus {

	private String statusCode;
	private String inquiryTypeCode;
	private String statusName;
	private String inquiryTypeName;
}
