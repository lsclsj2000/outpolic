package outpolic.user.mypage.service;

import java.util.List;

import org.springframework.stereotype.Service;

import outpolic.user.mypage.dto.UserInfoDTO;
import outpolic.user.review.dto.ReviewDTO;

@Service
public interface UserMypageEditService {
	// 특정 회원 수정
	void editUserInfo(UserInfoDTO user);
	// 특정 회원 정보 조회
	UserInfoDTO getUserInfoByCode(String memberCode);
	// 회원목록 조회
	List<UserInfoDTO> getMemberList();
	
	//회원 개인정보 중복검사
	boolean isUserInfoDuple(String type, String memberCode, String memberNickname, String memberEmail, String memberTelNo);
	
	ReviewDTO getUserReviewByCode(String memberCode);
}
