package outpolic.user.login.service.Impl;


import java.util.Map;
import java.util.HashMap;
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

	@Override
	public Map<String, Object> loginUser(String memberId, String memberPw) {
		Map<String, Object> resultMap = new HashMap<>();
		boolean isMatched = false;
		Member member = userLoginMapper.findMemberById(memberId);
	    if (member != null && member.getMemberPw().equals(memberPw)) {
	        isMatched = true;
	        resultMap.put("memberInfo", member);
	    }

	    resultMap.put("isMatched", isMatched);
	    return resultMap;
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
