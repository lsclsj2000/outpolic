package outpolic.user.login.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
public class UserLoginController {

    // 로그인 페이지 GET 요청
    @GetMapping("/pageLogin")
    public String showLoginPage() {
        return "user/login/userLoginView"; // templates/page-login.html
    }

    // 로그인 요청 처리 (POST)
    @PostMapping("/pageLogin")
    public String login(@RequestParam("email") String email,
                        @RequestParam("password") String password,
                        HttpSession session,
                        Model model) {

        // TODO: 실제 로그인 검증 로직 구현 (DB 연동 등)
        if (email.equals("test@example.com") && password.equals("1234")) {
            session.setAttribute("loginUser", email);
            return "redirect:/"; // 로그인 성공 시 홈으로
        } else {
            model.addAttribute("error", "이메일 또는 비밀번호가 올바르지 않습니다.");
            return "pagelogin"; // 로그인 실패 시 다시 로그인 페이지로
        }
    }

    // 비밀번호 재설정 페이지
    @GetMapping("/forgotPswd")
    public String forgotPasswordPage() {
        return "user/login/userForgotPswdView"; // templates/page-forgot-password.html
    }
    
    @GetMapping("/goodsList")
    public String userShopPage() {
        return "user/goods/goodsList";
    }

}
