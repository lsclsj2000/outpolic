package outpolic.user.mypage.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import outpolic.user.mypage.dto.UserInfoDTO;
import outpolic.user.review.dto.ReviewDTO;

@Mapper
public interface UserMypageEditMapper {

	//특정 회원 조회
	 UserInfoDTO getUserInfoByCode(String memberCode);
	 
	 // 특정 회원 수정
	 // 회원의 수정된 행 값을 리턴하기때문에 int를 사용한당
//	 int userEditInfo(UserMypage user);
	 int updateUserInfo(UserInfoDTO userInfo);
	 // 중복확인
	 boolean isNickNameDuplicated(@Param("memberNickname") String memberNickname, @Param("memberCode") String memberCode);
	 boolean isEmailDuplicated(@Param("memberEmail") String memberEmail, @Param("memberCode") String memberCode);
	 boolean isTelDuplicated(@Param("memberTelNo") String memberTelNo, @Param("memberCode") String memberCode);
	 
	/*
	 * int countByNickname(@Param("memberNickName") String memberNickName,
	 * 
	 * @Param("memberId") String memberId);
	 */
	 
	 ReviewDTO getUserReviewByCode(String memberCode);
}
