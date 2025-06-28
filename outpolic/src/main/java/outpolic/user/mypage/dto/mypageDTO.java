package outpolic.user.mypage.dto;

import java.util.List;

import lombok.Data;


@Data
public class mypageDTO {
	// 마이페이지 구현에 필요한 여러 dto의 집합
	// 마이페이지의 메인 화면에 노출시킬 몇 개인정보
	private String memberId;
    private String memberName;
    private String memberNickName;
    private String memberTelNo;
    private String memberJoinYmdt;

    private int portfolioCount;  // (추가 가능)
    private int reviewCount;
    private int mileageAmount;   // 또는 누적 포인트
	
	// user 개인정보 dto
	private List<userInfoDTO> userInfo;
	
	// user 외주 dto
	private List<userOsInfoDTO> userOsInfo;
	
	//USER 리뷰 DTO
	
	// user 즐겨찾기 dto
	
	// user 문의 dto
	
	// user 결제 dto
	
	// user 마일리지 dto
}
