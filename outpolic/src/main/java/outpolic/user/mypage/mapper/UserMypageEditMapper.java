package outpolic.user.mypage.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import outpolic.user.mypage.dto.UserInfoDTO;

@Mapper
public interface UserMypageEditMapper {

	//특정 회원 조회
	 UserInfoDTO getUserInfoById(String memberId);
	 
	 // 특정 회원 수정
	 // 회원의 수정된 행 값을 리턴하기때문에 int를 사용한당
//	 int userEditInfo(UserMypage user);
	 int updateUserInfo(UserInfoDTO userInfo);
	 
	boolean isNickNameDuplicated(@Param("memberNickName") String memberNickName, @Param("memberId") String memberId);
    boolean isEmailDuplicated(@Param("memberEmail") String memberEmail, @Param("memberId") String memberId);
    boolean isTelDuplicated(@Param("memberTelNo") String memberTelNo, @Param("memberId") String memberId);
	 
	int countByNickname(@Param("memberNickName") String memberNickName,
             			 @Param("memberId") String memberId);
}
