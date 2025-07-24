package outpolic.user.mypage.service;

import java.util.List;

import org.springframework.stereotype.Service;

import outpolic.common.dto.OutsourcingReviewDTO;
import outpolic.user.mypage.dto.UserInfoDTO;
import outpolic.user.outsourcing.dto.UserOsInfoDTO;
import outpolic.user.review.dto.ReviewDTO;

@Service
public interface UserMypageEditService {
	// 특정 회원 수정
	void editUserInfo(UserInfoDTO user);
	// 특정 회원 정보 조회
	UserInfoDTO getUserInfoByCode(String memberCode);

	
	//회원 개인정보 중복검사
	boolean isUserInfoDuple(String type, String memberCode, String memberNickname, String memberEmail, String memberTelNo);
	
	ReviewDTO getUserReviewByCode(String memberCode);
	
	List<OutsourcingReviewDTO> getOutsourcingReviewList(String memberCode);
	
	 // 완료된 외주 수 가져오기
	 int selectUserEndedOsByCode(String memberCode);
	 
	 //프로필 이미지 업로드
	 void updateProfileImg(String memberCode, String imagePath);
}
