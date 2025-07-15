package outpolic.enter.declaration.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping(value="/enter")
@RequiredArgsConstructor
public class EnterDeclarationController {
	
	
	@GetMapping("/enterDeclarationWrite")
	public String enterDeclarationWriteView(Model model) {
		// 신고 작성 페이지
		model.addAttribute("title", "신고 작성");
		
		return "enter/declaration/enterDeclarationWriteView";
	}
	
	
	@GetMapping("/enterDeclarationNotice")
	public String enterDeclarationNoticeView(Model model) {
		// 신고 주의사항 페이지
		model.addAttribute("title", "신고 주의사항");
		
		return "enter/declaration/enterDeclarationNoticeView";
	}

	
	@GetMapping("/enterDeclaration")
	public String enterDeclarationView(Model model) {
		// 나의 신고 내역 목록 페이지
		model.addAttribute("title", "신고 내역");
		
		return "enter/declaration/enterDeclarationView";
	}
	
	
	
}
