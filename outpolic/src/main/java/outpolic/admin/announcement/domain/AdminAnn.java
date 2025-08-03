package outpolic.admin.announcement.domain;

import lombok.Data;

@Data
public class AdminAnn {
	
	private String annCode;  
	private String annTitle; 
	private String annCn;    
	private String annStcCode;
	private String annAdmCode;
	private String annRegYmdt;
	private String annMdfcnAdmCode;
	private String annMdfcnYmdt;
}