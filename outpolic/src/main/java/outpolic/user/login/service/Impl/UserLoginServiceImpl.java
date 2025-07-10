package outpolic.user.login.service.Impl;


import java.util.HashMap;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.common.domain.Member;
import outpolic.user.login.mapper.UserLoginMapper;
import outpolic.user.login.service.UserLoginService;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserLoginServiceImpl implements UserLoginService {
	
	private final UserLoginMapper userLoginMapper;
	private final PasswordEncoder passwordEncoder;
	
	@Override
	public Map<String, Object> loginUser(String memberId, String memberPw) {
		Map<String, Object> resultMap = new HashMap<>();
		boolean isMatched = false;
		Member member = userLoginMapper.findMemberById(memberId);
	    if (member != null && passwordEncoder.matches(memberPw, member.getMemberPw())) {
	        isMatched = true;
	        resultMap.put("memberInfo", member);
	    }

	    resultMap.put("isMatched", isMatched);
	    return resultMap;
	}

	//member테이블에 마지막 로그인 일시 update
	@Override
	public void updateLoginDate(Member member) {
		userLoginMapper.updateMemberLoginDate(member);
		
	}


	
	
	
	
	/*
	 * @Override public Member loginUser(String memberId, String memberPw) { Member
	 * member =userLoginMapper.findMemberById(memberId);
	 * log.info("memberId: {}, pw: {}", memberId, memberPw);
	 * log.info("DB에서 찾은 회원: {}", member); if(member != null &&
	 * member.getMemberPw().equals(memberPw)) { return member; } return null;
	 * 
	 * }
	 */
}
