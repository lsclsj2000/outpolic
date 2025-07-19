package outpolic.user.declaration.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import outpolic.user.declaration.domain.UserDeclaration;
import outpolic.user.declaration.service.UserDeclarationService;

@Controller
@RequestMapping(value="/user")
@RequiredArgsConstructor
public class UserDeclarationController {
	
	private final UserDeclarationService declarationService;
	
	@GetMapping("/declarationReasons")
	@ResponseBody
	public List<UserDeclaration> getDeclarationReasons(@RequestParam("dtCd") String dtCd) {
		// 신고 사유 드롭다운 조회
	    return declarationService.getDeclarationReasonsByType(dtCd);
	}
	
	@GetMapping("/declarationTypes")
	@ResponseBody
	public List<UserDeclaration> getDeclarationTypes() {
		// 신고 타입 드롭다운 조회
	    return declarationService.getActiveDeclarationTypes();
	}
	
	@GetMapping("/declaration")
	public String declarationPage(HttpSession session, Model model) {
		// 신고 작성 시 세션 불러오기
	    String sid = (String) session.getAttribute("SID");
	    String sname = (String) session.getAttribute("SName");

	    if (sid != null && sname != null) {
	        model.addAttribute("reporter", sname + " (" + sid + ")");
	    } else {
	        model.addAttribute("reporter", "로그인이 필요합니다");
	    }

	    return "user/declarationForm";
	}
	
	@GetMapping("/userDeclarationWrite")
	public String userDeclarationWriteView(HttpSession session, Model model) {
		// 신고 작성 페이지
	    String sid = (String) session.getAttribute("SID");
	    String sname = (String) session.getAttribute("SName");
	    
	    System.out.println("세션 확인: SID = " + sid + ", SName = " + sname);
	    
	    if (sid != null && sname != null) {
	        model.addAttribute("reporter", sname + " (" + sid + ")");
	    } else {
	        model.addAttribute("reporter", "로그인이 필요합니다");
	    }

	    model.addAttribute("title", "신고 작성");
	    return "user/declaration/userDeclarationWriteView";
	}
	
	
	/*
	 * @GetMapping("/userDeclarationNotice") public String
	 * userDeclarationNoticeView(Model model) { // 신고 주의사항 페이지
	 * model.addAttribute("title", "신고 주의사항");
	 * 
	 * return "user/declaration/userDeclarationNoticeView"; }
	 * 
	 * 
	 * @GetMapping("/userDeclaration") public String userDeclarationView(Model
	 * model) { // 나의 신고 내역 목록 페이지 model.addAttribute("title", "신고 내역");
	 * 
	 * return "user/declaration/userDeclarationView"; }
	 */
	
	
	
}
