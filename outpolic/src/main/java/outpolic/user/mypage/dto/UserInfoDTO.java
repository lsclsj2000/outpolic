package outpolic.user.mypage.dto;

import java.time.LocalDate;

import lombok.Data;

// 회원의 개인정보에 대한 dto
@Data
public class UserInfoDTO {
	 public String memberId; 
	 public String memberName;
	 private String memberPw;
	 public String memberNickName; 
	 public String memberTelNo; 
	 public String memberAddress; 
	 public String memberDAddress; 
	 public String memberZip;
	 private String memberGender;
	 public String memberEmail; 
	 public LocalDate memberBirth;
	 public LocalDate memberJoinDate;
	 public LocalDate memberModifyDate;
}    
