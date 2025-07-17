package outpolic.admin.login.service.Impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import outpolic.admin.login.dto.AdminLoginDTO;
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
		System.out.println("[DEBUG] loginAdmin 메서드 진입");
		Map<String, Object> resultMap = new HashMap<>();
		boolean isMatched = false;
		
		AdminLoginDTO adminLoginDTO = adminLoginMapper.findAdminMemberById(memberId);
		
		if(adminLoginDTO != null && adminLoginDTO.getMemberInfo() != null) {
			Member member = adminLoginDTO.getMemberInfo();
			
			System.out.println("입력된 비밀번호(평문): " + memberPw);
			System.out.println("DB 저장된 비밀번호(암호): " + member.getMemberPw());
			System.out.println("매치 결과: " + passwordEncoder.matches(memberPw, member.getMemberPw()));

			if(member != null && passwordEncoder.matches(memberPw, member.getMemberPw())) {
				
				isMatched = true;
				resultMap.put("adminLoginDTO", adminLoginDTO); 
			}
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
