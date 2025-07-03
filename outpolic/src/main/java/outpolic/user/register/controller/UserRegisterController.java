package outpolic.user.register.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import outpolic.common.domain.Member;
import outpolic.user.register.service.UserRegisterService;

@Controller
public class UserRegisterController {
	
	private UserRegisterService userRegisterService;
    // 회원가입 페이지 시작
    // 기업회원, 일반회원 선택 페이지
    @GetMapping("/choiceRegister")
    public String choiceReg() {
    	return "user/register/userRegisterChoiceView";
    }
    // 일반회원 회원가입 페이지
    @GetMapping("/user/registerInfo")
    public String userReg() {
    	return "user/register/userRegisterView";
    }
    
    @PostMapping("/user/register")    
    public String userRegister(@ModelAttribute Member member) {

        String newMemberCode = userRegisterService.getNextMemberCode();
        member.setMemberCode(newMemberCode);
        userRegisterService.registerMember(member);
        return "redirect:/login"; // 또는 가입 완료 페이지
    }
    
}
