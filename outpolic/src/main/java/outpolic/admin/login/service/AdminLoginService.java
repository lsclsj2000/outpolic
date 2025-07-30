package outpolic.admin.login.service;

import java.util.Map;

import org.springframework.data.repository.query.Param;

import outpolic.common.domain.Member;

public interface AdminLoginService {
	Map<String, Object> loginAdmin(String memberId, String memberPw);
	
	public void updateAdminMemberLoginDate(Member member);
	
	
	// 회원의 가장 마지막 로그인 기록 가져오기
	public String getAdminLastLoginCode(String memberCode);
	
	//로그아웃 시간 업데이트
	public void updateAdminLogoutHistory(String loginHistoryCode);
}
