package outpolic.user.register.service.Impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import outpolic.common.domain.Member;
import outpolic.user.register.mapper.UserRegisterMapper;
import outpolic.user.register.service.UserRegisterService;

@Service
@RequiredArgsConstructor
public class UserRegisterServiceImpl implements UserRegisterService {
	
	
	private final UserRegisterMapper userRegisterMapper;
	
	@Override
	public String getNextMemberCode() {
		
		return userRegisterMapper.getNextMemberCode();
	}

	@Override
	public int registerMember(Member member) {
		
		return userRegisterMapper.UserRegister(member);
	}

	@Override
	public boolean isUserInfoDuple(String type, String value) {
		switch (type) {
        case "memberNickname":
            return userRegisterMapper.isNickNameDuplicated(value) >0;
        case "memberEmail":
            return userRegisterMapper.isEmailDuplicated(value) >0;
        case "memberTelNo":
            return userRegisterMapper.isTelDuplicated(value) >0;
        case "memberId":
            return userRegisterMapper.isIdDuplicated(value) >0;
        default:
		return false;
		}
	}
	
	@Override
	public String getRandomNickname() {
	    return userRegisterMapper.getRandomNickname();
	}
}


