package outpolic.enter.mypage.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class CorpInfo {
	private String corpCode; 
	private String corpBrno; 
	private String memberCode; 
	private String corpName; 
	private String corpRprsv; 
	private String corpTelNo; 
	private LocalDate corpFoundationYmdt; 
	private String corpAddress; 
	private String corpDAddress; 
	private String corpZip; 
	private String corpScale; 
	private String corpExplain; 
	private String corpHomepageUrl; 
	private String corpJoinYmdt; 
	private LocalDate corpModificationYmdt; 
	private String mbr2_cd; 
	private String corpScr; 
	private String statuseCode; 
	private int corpPortfolioCount; 
	private int corpOutsourcingCount;
}
