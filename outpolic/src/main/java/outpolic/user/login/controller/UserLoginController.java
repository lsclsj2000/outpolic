package outpolic.user.login.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.common.domain.Member;
import outpolic.user.login.service.UserLoginService;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserLoginController {
	
	private final UserLoginService userLoginService;
	
    // 로그인 페이지 GET 요청
    @GetMapping("/login")
    public String showLoginPage() {
        return "user/login/userLoginView"; 
    }

    // 로그인 요청 처리 (POST)
    @PostMapping("/login")
    public String login(@RequestParam String memberId,
                        @RequestParam String memberPw,
                        Member member,
                        HttpSession session,
                        RedirectAttributes redirectAttributes,
                        HttpServletRequest request,
                        Model model) {
   	  
    	  Map<String, Object> loginResult = userLoginService.loginUser(memberId, memberPw);
    	  boolean isMatched = (boolean) loginResult.get("isMatched");
    	  String clientIp = request.getRemoteAddr();
    	  String redirectUri = "redirect:/login";
        
        if (isMatched) {
        	Member memberInfo = (Member) loginResult.get("memberInfo");
            session.setAttribute("SID", memberInfo.getMemberId());
            session.setAttribute("SName", memberInfo.getMemberName());
            session.setAttribute("SGrd", memberInfo.getGradeCode());
            
        	redirectAttributes.addFlashAttribute("success", "로그인에 성공하였습니다");
        	log.info("로그인성공");
        	userLoginService.updateLoginDate(member);
            model.addAttribute("msg", "로그인에 성공하였습니다.");
            model.addAttribute("url", "/");
        	return "user/mypage/alert";
        } else {
        	model.addAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
        	redirectAttributes.addFlashAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
        	return "user/login/userLoginView";
        }
    }
    
    //로그아웃
    @GetMapping("/logout")
    public String logout(HttpSession session, Model model) {
        session.invalidate();  // 세션 전체 제거
        model.addAttribute("msg", "로그아웃되었습니다.");
		model.addAttribute("url", "/");
		return "user/mypage/alert";
    }
    

    // 비밀번호 재설정 페이지
    @GetMapping("/forgotPswd")
    public String forgotPasswordPage() {
        return "user/login/userForgotPswdView"; // templates/page-forgot-password.html
    }
    
}
