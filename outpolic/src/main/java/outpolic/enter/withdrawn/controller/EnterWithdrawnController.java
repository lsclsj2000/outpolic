package outpolic.enter.withdrawn.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class EnterWithdrawnController {
	
	// 기업유저 회원탈퇴 페이지
	@GetMapping("/enter/withdrawn")
	public String userWithdrawn(HttpSession session) {
		return "enter/withdrawn/enterWithdrawnView";
	}
	
}
