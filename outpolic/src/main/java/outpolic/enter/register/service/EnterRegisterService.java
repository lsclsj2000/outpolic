package outpolic.enter.register.service;

import outpolic.enter.mypage.dto.CorpInfo;

public interface EnterRegisterService {
	
	//기업 기본키 생성
	String getNextEnterCode();
	
	//기업 회원가입 처리
	int enterRegister(CorpInfo corpInfo);
	
	//사업자등록번호 중복 확인
	int isBrnoDuplicated(String corpBrno);
	
}
