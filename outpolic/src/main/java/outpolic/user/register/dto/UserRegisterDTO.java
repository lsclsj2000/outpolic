package outpolic.user.register.dto;

import lombok.Data;

@Data
public class UserRegisterDTO {
	private String memberId;
	private String memberPw;
	private String memberName;
	private String memberNickName;
	private String memberTelNo;
	private String memberEmail;
	private String memberAddress;
	private String memberDAddress;
	private String memberZip;
	private String memberGender;
	private String memberBirth;
	private String memberAgreeYN;
}
