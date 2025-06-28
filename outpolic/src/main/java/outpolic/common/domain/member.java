package outpolic.common.domain;

import lombok.Data;

@Data
public class member {
	private String memberCode;
    private String gradeCode;
    private String memberId;
//    private String memberPw;
    private String memberName;
    private String memberNickname;
    private String memberTelNo;
    private String memberAddress;
    private String memberDAddress;
    private String memberZip;
    private String memberEmail;
    private String memberBirth;
    private String memberGender;
    private String memberForegn;
    private String memberJoinYmdt;
    private String memberMdfcnYmdt;
    private String statusCode;
    private String memberAgreeYN;
    private String memberLastLoginYmdt;
    private String memberSocialLoginYN;

}
