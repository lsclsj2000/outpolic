package outpolic.user.register.service;

import org.springframework.stereotype.Service;

import outpolic.common.domain.Member;

@Service
public interface UserRegisterService {
	
	//중복확인
	boolean isUserInfoDuple(String type, String value);
	
	// 회원 코드(기본키) 생성
	String getNextMemberCode();
	
	// 회원가입 처리
	int registerMember(Member member);
	
	String getRandomNickname();
}
