package outpolic.enter.mypage.service;

import org.springframework.stereotype.Service;

import outpolic.enter.mypage.dto.EnterInfo;
import outpolic.user.mypage.dto.UserInfoDTO;

@Service
public interface EnterMypageService {
	
	// 특정 기업회원 조회하기
	EnterInfo getEnterInfoById(String memberId);
	
	// 특정 기업 개인정보 수정하기
	void editEnterInfo(EnterInfo enterInfo);
	
	// 특정 기업 개인정보 중복검사
	boolean isEnterInfoDuple(String type,String memberId,String memberNickname,String memberEmail,String memberTelNo);
}
