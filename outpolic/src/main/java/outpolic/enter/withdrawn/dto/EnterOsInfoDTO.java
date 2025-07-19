package outpolic.enter.withdrawn.dto;

import java.time.LocalDate;

import lombok.Data;

// 회원이 진행중인 외주에 대한 데이터를 받아올 dto
@Data
public class EnterOsInfoDTO {
	
	// outsourcing_contract_details
	private String ocdCd; 
	private String ocdReqType;
	private String entCd; 
	private String clCd; 
	private String ocdTtl; 
	private String ocdFrctnCmdty; 
	private String ocdDlvgdsMthd; 
	private String ocdExpln; 
	private LocalDate ocdDedline; 
	private String ocdAmt;  
	private LocalDate ocdDmndYmdt; 
	private LocalDate ocdRspnsYmdt; 
	private LocalDate ocdMdfcnYmdt; 
	private String ocdYn; 
	private LocalDate ocdYnYmdt; 
	private LocalDate ocdStrtYmdt;
	
	// outsourcing_prograss
	private String ospCd;  
	private String osStcCd; 
	private LocalDate ospSplyYmdt; 
	private String ospCustYn; 
	private LocalDate ospCustYmdt;
	
	//enterprise
	private String mbrCd;
	private String entNm;

}
