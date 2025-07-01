package outpolic.user.inquiry.domain;

import lombok.Data;

@Data
public class UserInquiryAttachment {
	private String saCode;        
    private String saReferCode; 
    private String saOrgnlName;     
    private String saSrvrName;      
    private String saPath;        
    private String saExtn;        
    private int saSize;          
    private String mbrCode;         
    private String saRegYmdt;
}