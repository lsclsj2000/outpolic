package outpolic.user.mypage.mapper;

import org.apache.ibatis.annotations.Mapper;

import outpolic.user.mypage.dto.userInfoDTO;

@Mapper
public interface UserMypageEditMapper {

	//특정 회원 조회
	 userInfoDTO getUserInfoById(String memberId);
	 
	 // 특정 회원 수정
	 // 회원의 수정된 행 값을 리턴하기때문에 int를 사용한당
//	 int userEditInfo(UserMypage user);
	 int updateUserInfo(userInfoDTO userInfo);
}
