package outpolic.user.login.service;

import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public interface UserLoginService {
	Map<String, Object> loginUser(String memberId, String memberPw);
}
