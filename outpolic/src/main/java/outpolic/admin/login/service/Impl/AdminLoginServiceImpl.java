package outpolic.admin.login.service.Impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import outpolic.admin.login.mapper.AdminLoginMapper;
import outpolic.admin.login.service.AdminLoginService;
import outpolic.common.domain.Member;

@Service
@RequiredArgsConstructor
public class AdminLoginServiceImpl implements AdminLoginService {
	
	private final AdminLoginMapper adminLoginMapper;
	private final PasswordEncoder passwordEncoder;

	@Override
	public Map<String, Object> loginAdmin(String memberId, String memberPw) {
		Map<String, Object> resultMap = new HashMap<>();
		boolean isMatched = false;
		Member member = adminLoginMapper.findAdminMemberById(memberId);
		if(member != null && passwordEncoder.matches(memberPw, member.getMemberPw())) {
			isMatched = true;
			resultMap.put("memberInfo", member);
		}
		resultMap.put("isMatched", isMatched);
		return resultMap;
	}
	
	//member테이블에 마지막 로그인 일시 update
	@Override
	public void updateAdminMemberLoginDate(Member member) {
		adminLoginMapper.updateAdminMemberLoginDate(member);
	}

}
