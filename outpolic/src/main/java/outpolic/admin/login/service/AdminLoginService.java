package outpolic.admin.login.service;

import java.util.Map;

import outpolic.common.domain.Member;

public interface AdminLoginService {
	Map<String, Object> loginAdmin(String memberId, String memberPw);
	
	public void updateAdminMemberLoginDate(Member member);
}
