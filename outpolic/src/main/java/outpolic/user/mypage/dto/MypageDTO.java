package outpolic.user.mypage.dto;

import java.util.List;

import lombok.Data;
import outpolic.user.review.dto.ReviewDTO;


@Data
public class MypageDTO {
	// 마이페이지 구현에 필요한 여러 dto의 집합
	// 마이페이지의 메인 화면에 노출시킬 몇몇 개인정보
	private String memberId;
	private String memberCode;
    private String memberName;
    private String memberNickname;
    private String memberTelNo;
    private String memberJoinYmdt;

//    private int portfolioCount;  // (추가 가능)
    private int reviewCount;
    private int mileageAmount;   
	
	// user 개인정보 dto
	private List<UserInfoDTO> userInfo;
	
	// user 외주 dto
	private List<UserOsInfoDTO> userOsInfo;
	
	//USER 리뷰 DTO
	private List<ReviewDTO> userReview;
	// user 즐겨찾기 dto
	
	// user 문의 dto
	
	// user 결제 dto
	
	// user 마일리지 dto
}
