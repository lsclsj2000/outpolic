package outpolic.user.login.service.Impl;


import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import outpolic.common.domain.Member;
import outpolic.user.login.mapper.UserLoginMapper;
import outpolic.user.login.service.UserLoginService;

@Service
@RequiredArgsConstructor
public class UserLoginServiceImpl implements UserLoginService {
	private final UserLoginMapper userLoginMapper;
	
	
	@Override
	public Member loginUser(String memberId, String memberPw) {
		Member member =userLoginMapper.findMemberById(memberId);
		
		if(member != null && member.getMemberPw().equals(memberPw)) {
			return member;
		}
		
		return null;
	}
}
