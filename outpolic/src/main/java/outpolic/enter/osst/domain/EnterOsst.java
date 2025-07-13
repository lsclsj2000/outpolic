package outpolic.enter.osst.domain;

import lombok.Data;

@Data
public class EnterOsst {
	private String ospCode;   
	private String ocdCode;  
	             
	private String ocdTitle;  
	private String ocdExpln;
	private String ocdDmndYmdt;
	private String ocdDedline;
	private Integer ocdAmt;    
	             
	private Integer ospCustYn; 
	private String ospSplyYmdt;
	private String ospCustYmdt;
	private String ospCustStcYn;
	private Integer ospStcOrder;
	             
	private String memberCode;
	private String memberName;
	private String memberEmail;
	             
	private String entCode;
	private String entName;
	private String entMbrName;
	private String entMbrEmail;
	private String entMemberCode;
	private String entMemberName;
	             
	private String stcCode;
	private String stcName;
}