package outpolic.admin.announcement.domain;

import java.util.List;

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
	
	private String saCode;
	private String saOrgnlName;
	private String saSrvrName;
	private String saPath;
	private String saExtn;
	private int saSize;   
	
	private List<AdminAnnFile> adminAnnFiles;
}