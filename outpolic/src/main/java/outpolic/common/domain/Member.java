package outpolic.common.domain;

import java.time.LocalDate;

import lombok.Data;

@Data
public class Member {
	private String memberCode;
    private String gradeCode;
    private String memberId;
    private String memberPw;
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
    private LocalDate memberJoinYmdt;
    private LocalDate memberMdfcnYmdt;
    private String statusCode;
    private String memberAgreeYN;
    private LocalDate memberLastLoginYmdt;
    private String memberSocialLoginYN;
    
    //회원 탈퇴 예정일
    private LocalDate withdrawalExpectedDate;

}
