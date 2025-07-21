package outpolic.user.passwordChange.service.Impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import outpolic.common.domain.Member;
import outpolic.user.passwordChange.mapper.UserPasswordChangeMapper;
import outpolic.user.passwordChange.service.UserPasswordChangeService;

@Service
@RequiredArgsConstructor
public class UserPasswordChangeServiceImpl implements UserPasswordChangeService {

	private final PasswordEncoder passwordEncoder;
	private final UserPasswordChangeMapper userPasswordChangeMapper;
	
	@Override
	public boolean isCurrentPasswordValid(String memberCode, String rawPw) {
		 String encodedPw = userPasswordChangeMapper.getEncodedPassword(memberCode);
		    return passwordEncoder.matches(rawPw, encodedPw);
	}

	@Override
	public void updatePassword(String memberCode, String rawNewPw) {
		System.out.println("[DEBUG] updatePassword() 호출됨");
		String encodedPw = passwordEncoder.encode(rawNewPw);
		userPasswordChangeMapper.updateMemberMdfcn(memberCode, encodedPw);
	}

	@Override
	public String getEncodedPassword(String memberCode) {
		return userPasswordChangeMapper.getEncodedPassword(memberCode);
	}
	



}
