package outpolic.user.login.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import outpolic.common.domain.Member;

@Service
public interface UserLoginService {
	Map<String, Object> loginUser(String memberId, String memberPw);
	//마지막 로그인 일시 업데이트
	public void updateLoginDate(Member member);
}
