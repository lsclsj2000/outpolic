package outpolic.user.login.service;

import org.springframework.stereotype.Service;

import outpolic.common.domain.Member;

@Service
public interface UserLoginService {
	Member loginUser(String memberId, String memberPw);
}
