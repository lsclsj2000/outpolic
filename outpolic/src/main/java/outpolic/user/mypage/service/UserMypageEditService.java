package outpolic.user.mypage.service;

import java.util.List;

import org.springframework.stereotype.Service;

import outpolic.user.mypage.dto.userInfoDTO;

@Service
public interface UserMypageEditService {
	// 특정 회원 수정
	void editUserInfo(userInfoDTO user);
	// 특정 회원 정보 조회
	userInfoDTO getUserInfoById(String memberId);
	// 회원목록 조회
	List<userInfoDTO> getMemberList();
}
