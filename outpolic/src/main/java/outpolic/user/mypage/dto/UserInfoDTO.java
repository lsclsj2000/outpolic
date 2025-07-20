package outpolic.user.mypage.dto;

import java.time.LocalDate;

import lombok.Data;

// 회원의 개인정보에 대한 dto
@Data
public class UserInfoDTO {
	private String memberId; 
	private String memberCode;
	private String memberName;
	private String memberPw;
	private String memberNickname; 
	private String memberTelNo; 
	private String memberAddress; 
	private String memberDAddress; 
	private String memberZip;
	private String memberGender;
	private String memberEmail; 
	private LocalDate memberBirth;
	private LocalDate memberJoinDate;
	private LocalDate memberModifyDate;
	//회원 프로필 이미지
    private String memberImg;
}    
