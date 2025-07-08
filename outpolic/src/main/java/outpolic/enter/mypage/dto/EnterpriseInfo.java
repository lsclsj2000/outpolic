package outpolic.enter.mypage.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class EnterpriseInfo {
	private String enterCode; 
	private String enterBrno; 
	private String memberCode; 
	private String enterName; 
	private String enterRprsv; 
	private String enterTelNo; 
	private LocalDate enterFoundationYmdt; 
	private String enterAddress; 
	private String enterDAddress; 
	private String enterZip; 
	private String enterScale; 
	private String enterExplain; 
	private String enterHomepageUrl; 
	private String enterJoinYmdt; 
	private LocalDate enterModificationYmdt; 
	private String mbr2_cd; 
	private String enterScr; 
	private String statuseCode; 
	private int enterPortfolioCount; 
	private int enterOutsourcingCount;
}
