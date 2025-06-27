package outpolic.enter.register.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class enterRegisterController {
	// 기업 회원가입 사업자정보 받는 페이지
    @GetMapping("/enterRegAdd")
    public String enterRegAdd() {
    	return "enter/register/enterRegisterAddView";
    }
    // 기업 회원가입 페이지
    @GetMapping("/enterRegister")
    public String enterReg() {
    	return "enter/register/enterRegisterView";
    }
    
}
