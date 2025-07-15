package outpolic.user.declaration.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping(value="/user")
@RequiredArgsConstructor
public class UserDeclarationController {
	
	
	@GetMapping("/userDeclarationWrite")
	public String userDeclarationWriteView(Model model) {
		// 신고 작성 페이지
		model.addAttribute("title", "신고 작성");
		
		return "user/declaration/userDeclarationWriteView";
	}
	
	
	@GetMapping("/userDeclarationNotice")
	public String userDeclarationNoticeView(Model model) {
		// 신고 주의사항 페이지
		model.addAttribute("title", "신고 주의사항");
		
		return "user/declaration/userDeclarationNoticeView";
	}

	
	@GetMapping("/userDeclaration")
	public String userDeclarationView(Model model) {
		// 나의 신고 내역 목록 페이지
		model.addAttribute("title", "신고 내역");
		
		return "user/declaration/userDeclarationView";
	}
	
	
	
}
