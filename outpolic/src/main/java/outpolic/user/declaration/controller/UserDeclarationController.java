package outpolic.user.declaration.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping(value="/user")
@RequiredArgsConstructor
public class UserDeclarationController {
	
	
	@GetMapping("/userDeclarationWriteNotice")
	public String userDeclarationWriteView() {
		// 신고 작성 페이지
		
		return "user/declaration/userDeclarationWriteView";
	}
	
	
	@GetMapping("/userDeclarationNotice")
	public String userDeclarationNoticeView() {
		// 신고 주의사항 페이지
		
		return "user/declaration/userDeclarationNoticeView";
	}

	
	@GetMapping("/userDeclaration")
	public String userDeclarationView() {
		// 나의 신고 내역 목록 페이지
		
		return "user/declaration/userDeclarationView";
	}
	
	
	
}
