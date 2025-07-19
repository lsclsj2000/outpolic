package outpolic.user.withdrawn.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import outpolic.user.withdrawn.dto.UserWithdrawnDTO;
import outpolic.user.withdrawn.mapper.UserWithdrawnMapper;
import outpolic.user.withdrawn.service.UserWithdrawnService;

@Controller
@RequiredArgsConstructor
public class UserWithdrawnController {
	
	private final UserWithdrawnService userWithdrawnService;
	private final UserWithdrawnMapper userWithdrawnMapper;
	private final PasswordEncoder passwordEncoder;
	
	// 일반유저 회원탈퇴 페이지
	@GetMapping("/user/withdrawn")
	public String userWithdrawn(HttpSession session) {
		String memberCode = (String) session.getAttribute("SCD");
		
		if(memberCode == null) {
			return "redirect :/login";
		}
		return "user/withdrawn/userWithdrawnView";
	}
	
	@PostMapping("/user/withdrawn")
	@ResponseBody
	public String userWithdrawClick(@RequestParam String memberPw,
	                       @RequestParam(required = false) String wmRsn,
	                       HttpSession session,
	                       Model model) {
		String memberCode = (String) session.getAttribute("SCD");
		if (memberCode == null) {
			model.addAttribute("msg", "로그인이 만료되었습니다.");
            model.addAttribute("url", "/login");
	        return "user/mypage/alert"; // 또는 401 처리
	    }
		UserWithdrawnDTO dto = userWithdrawnMapper.getWithdrawnMemberInfoByCode(memberCode); 
	    if (dto == null || !passwordEncoder.matches(memberPw, dto.getMemberPw())) {
	        return "비밀번호가 일치하지 않습니다.";
	    }
	    if (wmRsn == null || wmRsn.trim().isEmpty()) {
	    	wmRsn = "사유 미입력";
	    }
	    
	    userWithdrawnService.withdrawMember(memberCode, wmRsn);
	    
	    //세션초기화
	    session.invalidate();
	    
	    model.addAttribute("msg", "탈퇴가 완료되었습니다.");
	    model.addAttribute("url", "/"); // 메인으로 이동
	    return "user/mypage/alert"; 

	}
	
}
