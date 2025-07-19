package outpolic.user.withdrawn.controller;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import outpolic.user.outsourcing.dto.UserOsInfoDTO;
import outpolic.user.outsourcing.service.UserOutsourcingService;
import outpolic.user.withdrawn.dto.UserWithdrawnDTO;
import outpolic.user.withdrawn.mapper.UserWithdrawnMapper;
import outpolic.user.withdrawn.service.UserWithdrawnService;

@Controller
@RequiredArgsConstructor
public class UserWithdrawnController {
	
	private final UserWithdrawnService userWithdrawnService;
	private final UserWithdrawnMapper userWithdrawnMapper;
	private final PasswordEncoder passwordEncoder;
	private final UserOutsourcingService userOutsourcingService;
	
	// 일반유저 회원탈퇴 페이지
	@GetMapping("/user/withdrawn")
	public String userWithdrawn(HttpSession session, Model model) {
		String memberCode = (String) session.getAttribute("SCD");
		List<UserOsInfoDTO> ingOs = userOutsourcingService.UserOsIngSelectByCode(memberCode);
		int ingOsCount = (ingOs != null) ? ingOs.size() : 0;
		model.addAttribute("ingOsCount", ingOsCount);
		if(memberCode == null) {
			return "redirect:/login";
		}
		return "user/withdrawn/userWithdrawnView";
	}
	
	@PostMapping("/user/withdrawn")
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
	    
	    List<UserOsInfoDTO> ingOsList = userOutsourcingService.UserOsIngSelectByCode(memberCode);
	    if (ingOsList != null && !ingOsList.isEmpty()) {
	        model.addAttribute("msg", "진행 중인 외주가 있어 탈퇴할 수 없습니다.");
	        model.addAttribute("ingOsCount", ingOsList.size()); // 다시 넘겨줌
	        return "user/withdrawn/userWithdrawnView"; 
	        }
	    
	    userWithdrawnService.withdrawMember(memberCode, wmRsn);
	    
	    //세션초기화
	    session.invalidate();
	    
	    model.addAttribute("msg", "탈퇴가 완료되었습니다.");
	    model.addAttribute("url", "/"); // 메인으로 이동
	    return "user/mypage/alert"; 

	}
	
}
