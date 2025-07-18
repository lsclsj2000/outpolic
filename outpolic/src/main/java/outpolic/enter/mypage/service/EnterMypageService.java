package outpolic.enter.mypage.service;

import java.util.List;

import org.springframework.stereotype.Service;

import outpolic.common.dto.OutsourcingReviewDTO;
import outpolic.enter.mypage.dto.CorpInfo;
import outpolic.enter.mypage.dto.EnterInfo;
import outpolic.user.mypage.dto.UserInfoDTO;

@Service
public interface EnterMypageService {
	
	// 특정 기업회원 조회하기
	EnterInfo getEnterInfoByCode(String memberCode);
	
	// 특정 기업 개인정보 수정하기
	void editEnterInfo(EnterInfo enterInfo);
	
	// 특정 기업 개인정보 중복검사
	boolean isEnterInfoDuple(String type,String memberCode,String memberNickname,String memberEmail,String memberTelNo);

	// 특정 기업정보 조회
	CorpInfo getEnterpriseInfoByCode(String memberCode);
	
	
	// 특정 기업정보 수정
	void editEnterpriseInfo(CorpInfo corpInfo);
	
	// 리뷰-외주 연결
	List<OutsourcingReviewDTO> getOutsourcingReviewList(String memberCode);
	
	// 특정 기업 외주글 조회
	
	// 특정 기업 포폴글 조회
}
