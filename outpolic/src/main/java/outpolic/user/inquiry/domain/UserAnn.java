package outpolic.user.inquiry.domain;

import java.util.List;

import lombok.Data;

@Data
public class UserAnn {
	private String annCode;
	private String annType; 
	private String annTitle;
	private String annCn;  
	private String stcCode;
	private String admCode;
	private String mbrCode;
	private String mbrName;
	private String annRegYmdt;
	
	private String saCode;
	private String saOrgnlName;
	private String saSrvrName;
	private String saPath;
	private String saExtn;
	private String saSize;

	private List<UserInquiryFile> userInquiryFiles;
}
