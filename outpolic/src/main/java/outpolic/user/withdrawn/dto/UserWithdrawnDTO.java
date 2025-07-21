package outpolic.user.withdrawn.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class UserWithdrawnDTO {
	//뭘 가져와야하는가
	
	// member 기본데이터
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
	
	// 북마크 날리기
    private String bookmarkCode;
    private String contentListCode;
    private LocalDate bookmarkRegYmdt;
    
	// 권한테이블에서 데이터 날리기
    
    
	// 마일리지 내역 날리기
    private String ptsCd; 
    private String ptsPoints; 
    private String ptsPointsDelta; 
    private String ptsCumPoints; 
    private String ptsStatus; 
    private LocalDate ptsYmdt;
    
    // 탈퇴회원 테이블에 데이터 삽입
    private String wmCd; 
    private String wmMbrCd; 
    private String wmGrdCd; 
    private String wmRsn; 
    private LocalDate wmYmdt; 
    private int wmActiveDays; 
    private String wmType;
    
	
}
