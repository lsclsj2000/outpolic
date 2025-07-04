package outpolic.user.login.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        return "user/login/userLoginView"; // templates/page-login.html
    }

    // 로그인 요청 처리 (POST)
    @PostMapping("/login")
    public String login(@RequestParam String memberId,
                        @RequestParam String memberPw,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {
    	
    	  Member loginMember = userLoginService.loginUser(memberId, memberPw);

        
        if (loginMember != null) {
        	session.setAttribute("loginMember", loginMember);
        	redirectAttributes.addFlashAttribute("success", "로그인에 성공하였습니다");
        	log.info("로그인성공");
            return "redirect:/"; 
        } else {
        	redirectAttributes.addFlashAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
            return "redirect:/login";
        }
    }

    // 비밀번호 재설정 페이지
    @GetMapping("/forgotPswd")
    public String forgotPasswordPage() {
        return "user/login/userForgotPswdView"; // templates/page-forgot-password.html
    }
    
	/*
	 * @GetMapping("/goodsList") public String userShopPage() { return
	 * "user/goods/goodsList"; }
	 */

}
