package outpolic.user.register.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
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
        case "memberNickName":
            return userRegisterMapper.isNickNameDuplicated(value);
        case "memberEmail":
            return userRegisterMapper.isEmailDuplicated(value);
        case "memberTelNo":
            return userRegisterMapper.isTelDuplicated(value);
        case "memberId":
            return userRegisterMapper.isIdDuplicated(value);
        default:
		return false;
		}
	}
}


