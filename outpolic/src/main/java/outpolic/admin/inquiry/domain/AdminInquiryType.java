package outpolic.admin.inquiry.domain;

import lombok.Data;

@Data
public class AdminInquiryType {
	private String inquiryTypeCode;
	private String inquiryTypeName;
	private String inquiryTypeExpln;
	private String statusCode;
	private String inquiryTypeRegAdm;
	private String inquiryTypeRegDate;
	private String inquiryTypeMdfcnAdm;
	private String inquiryTypeMdfcnDate;

    // Lombok 설정에 문제가 있다면 아래 Getter/Setter를 추가
    // public String getInquiryTypeCode() { return inquiryTypeCode; }
    // public void setInquiryTypeCode(String inquiryTypeCode) { this.inquiryTypeCode = inquiryTypeCode; }
}