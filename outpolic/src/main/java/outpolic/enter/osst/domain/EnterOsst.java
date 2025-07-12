package outpolic.enter.osst.domain;

import lombok.Data;

@Data
public class EnterOsst {
	private String ospCode;
	private String ospOcdCode; 
	private String ospOcdTitle;
	private String ospMemberCode;
	private String ospMemberName;
	private String ospEntCode;
	private String ospEntMemberCode;
	private String ospEntMemberName;
	private String ospEntName; 
	private String ospStcCode;  
	private String ospStcName; 
	private String ospSplyYmdt;  
	private Integer ospCustYn; 
	private String ospCustYmdt;
	private String ospCustStcYn;
	private Integer ospStcOrder;
	
	
	
	private String ocdCode;   
	private String ocdMemberCode;
	private String ocdTitle;    
	private String ocdExpln; 
	private String ocdDmndYmdt;
	
	private String osstDetailCode;
	private String osstDetailMemberCode;
	private String osstDetailMemberName;
	private String osstDetailEntCode;
	private String osstDetailEntName;  
	private String osstDetailEntMbrName;
	private String osstDetailTitle;
	private String osstDetailExpln;  
	private String osstDetailDedline;  
	private String osstDetailAmt;  
	private String osstDetailStcCode;
	private String osstDetailDmndYmdt;
}