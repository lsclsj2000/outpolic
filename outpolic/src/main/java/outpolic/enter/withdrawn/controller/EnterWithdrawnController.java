package outpolic.enter.withdrawn.controller;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import outpolic.enter.withdrawn.dto.EnterOsInfoDTO;
import outpolic.enter.withdrawn.dto.EnterWithdrawnDTO;
import outpolic.enter.withdrawn.mapper.EnterWithdrawnMapper;
import outpolic.enter.withdrawn.service.EnterWithdrawnService;

@Controller
@RequiredArgsConstructor
public class EnterWithdrawnController {
	
	private final EnterWithdrawnService enterWithdrawnService;
	private final EnterWithdrawnMapper enterWithdrawnMapper;
	private final PasswordEncoder passwordEncoder;

	
	// 기업유저 회원탈퇴 페이지
	@GetMapping("/enter/withdrawn")
	public String userWithdrawn(HttpSession session, Model model) {
		
		String memberCode = (String) session.getAttribute("SCD");
		
		List<EnterOsInfoDTO> ingOs = enterWithdrawnService.EnterOsIngSelectByCode(memberCode);
		
		int ingOsCount = (ingOs !=null) ? ingOs.size() : 0;
		model.addAttribute("ingOsCount", ingOsCount);
		if(memberCode == null) {
			return "redirect:/login";
		}
		
		return "enter/withdrawn/enterWithdrawnView";
	}
	
	@PostMapping("/enter/withdrawn")
	public String enterWithrawnClick(@RequestParam String memberPw,
			                         @RequestParam(required = false) String wmRsn,
			                         HttpSession session,
			                         Model model) {
		System.out.println("탈퇴 요청 들어옴");
		String memberCode = (String) session.getAttribute("SCD");
		if (memberCode == null) {
			model.addAttribute("msg", "로그인이 만료되었습니다.");
            model.addAttribute("url", "/login");
	        return "enter/mypage/alert"; // 또는 401 처리
	    }
		
		EnterWithdrawnDTO dto = enterWithdrawnMapper.getWithdrawnEnterMemberInfoByCode(memberCode);
		if (dto == null || !passwordEncoder.matches(memberPw, dto.getMemberPw())) {
			model.addAttribute("msg", "비밀번호가 일치하지 않습니다.");
		    model.addAttribute("url", "/enter/withdrawn");
		    return "enter/mypage/alert";
	    }
	    if (wmRsn == null || wmRsn.trim().isEmpty()) {
	    	wmRsn = "사유 미입력";
	    }
		List<EnterOsInfoDTO> ingOsList = enterWithdrawnService.EnterOsIngSelectByCode(memberCode);
		if(ingOsList != null && !ingOsList.isEmpty()) {
			model.addAttribute("msg", "진행 중인 외주가 있어 탈퇴할 수 없습니다.");
	        model.addAttribute("ingOsCount", ingOsList.size()); // 다시 넘겨줌
	        return "enter/withdrawn/enterWithdrawnView"; 
		}
	    
	    enterWithdrawnService.withdrawnEnterMember(memberCode, wmRsn);
	    
	    //세션초기화 후 메인페이지로 이동
	    session.invalidate();
	    
	    model.addAttribute("msg", "탈퇴가 완료되었습니다.");
	    model.addAttribute("url", "/"); // 메인으로 이동
	    return "enter/mypage/alert"; 
	}
	
}
