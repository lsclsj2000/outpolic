package outpolic.enter.mypage.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class EnterInfo {
	private String memberCode;
	private String memberId; 
	private String gradeCode;
	private String memberName;
	private String memberPw;
	private String memberNickname; 
	private String memberTelNo; 
	private String memberAddress; 
	private String memberDAddress; 
	private String memberZip;
	private String memberGender;
	private String memberEmail; 
	private String statusCode;
	private LocalDate memberBirth;
	private LocalDate memberJoinDate;
	private LocalDate memberModifyDate;
	
	private String mbrImg;
}
