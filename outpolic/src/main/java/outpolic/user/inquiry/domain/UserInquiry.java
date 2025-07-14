package outpolic.user.inquiry.domain;

import java.util.List;

import lombok.Data;

@Data
public class UserInquiry {
	private String inquiryCode;
	private String memberName;
	private String inquiryTypeName;
	private String inquiryTitle;
	private String inquiryCn;
	private int inquirySecret;
	private String inquiryRegDate;
	private String inquiryProcessCn;
	private String adminCode;
	private String admMbrCode;
	private String inquiryTypeCode;
	private String memberCode;
	
	private String inquiryTypeExpln; 
	private String statusCode;
	private String statusName;
	private String inquiryProcessCode;
	private String inquiryTypeRegAdm; 
	private String inquiryTypeRegDate;
	private String inquiryTypeMdfcnAdm; 
	private String inquiryTypeMdfcnDate;
	
	private String inquiryProcessAnsCn;
    private String inquiryProcessRegDate;
    private String adminName;
    
    private List<UserInquiryFile> userInquiryFiles;
    
    
}