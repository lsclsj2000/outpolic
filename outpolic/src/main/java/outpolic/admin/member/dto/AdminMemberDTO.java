package outpolic.admin.member.dto;

import java.time.LocalDate;

import lombok.Data;
@Data
public class AdminMemberDTO {
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
	private String adminCode; 
	private String corpScr; 
	private String statusCode; 
	private int corpPortfolioCount; 
	private int corpOutsourcingCount;
	
	private String entImg;

}
