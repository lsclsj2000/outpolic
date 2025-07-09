package outpolic.user.register.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import outpolic.common.domain.Member;
import outpolic.user.register.service.UserRegisterService;

@Controller
@RequiredArgsConstructor
public class UserRegisterController {
	
	private final UserRegisterService userRegisterService;
    // 회원가입 페이지 시작
    // 기업회원, 일반회원 선택 페이지
	/*
	 * @GetMapping("/choiceRegister") public String choiceReg() { return
	 * "user/register/userRegisterChoiceView"; }
	 */
    // 일반회원 회원가입 페이지
    @GetMapping("/user/registerInfo")
    public String userReg() {
    	return "user/register/userRegisterView";
    }
    
    @PostMapping("/user/register")    
    public String userRegister(@ModelAttribute Member member, Model model) {
    	if (userRegisterService.isUserInfoDuple("memberId", member.getMemberId())) {
            model.addAttribute("errorMsg", "이미 사용 중인 아이디입니다.");
            return "user/register/userRegisterView";
        }

        if (userRegisterService.isUserInfoDuple("memberEmail", member.getMemberEmail())) {
            model.addAttribute("errorMsg", "이미 사용 중인 이메일입니다.");
            return "user/register/userRegisterView";
        }

        if (userRegisterService.isUserInfoDuple("memberTelNo", member.getMemberTelNo())) {
            model.addAttribute("errorMsg", "이미 사용 중인 전화번호입니다.");
            return "user/register/userRegisterView";
        }

        if (userRegisterService.isUserInfoDuple("memberNickname", member.getMemberNickname())) {
            model.addAttribute("errorMsg", "이미 사용 중인 닉네임입니다.");
            return "user/register/userRegisterView";
        }

        String newMemberCode = userRegisterService.getNextMemberCode();
        member.setMemberCode(newMemberCode);
        
        if (member.getMemberNickname() == null || member.getMemberNickname().trim().isEmpty()) {
            String randomNickname = userRegisterService.getRandomNickname();
            member.setMemberNickname(randomNickname);
        }
        
        userRegisterService.registerMember(member);
        model.addAttribute("msg", "회원가입이 완료되었습니다. 로그인해주세요");
        model.addAttribute("url", "/login");
        return "user/mypage/alert"; // 또는 가입 완료 페이지
    }
    
    @GetMapping("/user/checkDuplicate")
    @ResponseBody
    public Map<String, Boolean> checkDuplicate(@RequestParam String type,
                                               @RequestParam String value) {
        boolean isUserInfoDuple = userRegisterService.isUserInfoDuple(type, value);
        return Map.of("duplicate", isUserInfoDuple);
    }
    
}
