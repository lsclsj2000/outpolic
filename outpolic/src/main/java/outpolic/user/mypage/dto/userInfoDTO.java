package outpolic.user.mypage.dto;

import java.time.LocalDate;

import lombok.Data;

// 회원의 개인정보에 대한 dto
@Data
public class userInfoDTO {
	 public String memberId; 
	 public String memberName; 
	 public String memberNickName; 
	 public String memberTelNo; 
	 public String memberAddress; 
	 public String memberDAddress; 
	 public String memberZip; 
	 public String memberEmail; 
	 public LocalDate memberBirth;
	 public LocalDate memberJoinDate;
}    
