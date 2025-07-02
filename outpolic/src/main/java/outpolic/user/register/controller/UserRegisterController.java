package outpolic.user.register.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserRegisterController {
    // 회원가입 페이지 시작
    @GetMapping("/pageRegister")
    public String mainRegPage() {
        return "user/register/userRegisterMainView"; // templates/page-register.html
    }
    // 기업회원, 일반회원 선택 페이지
    @GetMapping("/choiceRegister")
    public String choiceReg() {
    	return "user/register/userRegisterChoiceView";
    }
    // 일반회원 회원가입 페이지
    @GetMapping("/userRegister")
    public String userReg() {
    	return "user/register/userRegisterView";
    }
    
    
    
}
