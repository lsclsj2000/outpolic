package outpolic.user.login.service.Impl;

import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import outpolic.user.login.dto.LoginFailDTO;
import outpolic.user.login.mapper.UserLoginLimitMapper;
import outpolic.user.login.service.LoginLimitService;

@Service
@RequiredArgsConstructor
public class loginLimitServiceImpl implements LoginLimitService {

	private final UserLoginLimitMapper userLoginLimitMapper;
	
	@PostConstruct
    public void init() {
        System.out.println("🧪 userLoginLimitMapper = " + userLoginLimitMapper);
    }
	
	@Override
	public LoginFailDTO getLimitInfo(String memberCode) {
		
		return userLoginLimitMapper.getLimitInfoByMemberCode(memberCode);
	}

}
