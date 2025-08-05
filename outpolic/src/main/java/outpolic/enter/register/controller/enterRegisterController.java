package outpolic.enter.register.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.enter.mypage.dto.CorpInfo;
import outpolic.enter.register.service.EnterRegisterService;

@Controller
@RequiredArgsConstructor
@Slf4j
public class enterRegisterController {
	
	private final EnterRegisterService enterRegisterService;
	
	// 기업 회원 전환 사업자정보 받는 페이지
    @GetMapping("/enterRegAdd")
    public String enterRegAdd(HttpSession session, CorpInfo corpInfo, Model model) {
    	String memberCode = (String) session.getAttribute("SCD");
    	corpInfo.setMemberCode(memberCode);
    	if(memberCode == null) {
    		model.addAttribute("msg", "로그인 후 이용해주세요");
    		model.addAttribute("url", "/");
    		return "/user/mypage/alert";
    	}
    	return "enter/register/enterRegisterAddView";
    }
    
    @PostMapping("/enter/register")
    public String enterpriseRegister(@ModelAttribute CorpInfo corpInfo, Model model, HttpSession session) {
        // 새 기업코드 생성 후 설정
        String newEnterCode = enterRegisterService.getNextEnterCode();
        corpInfo.setCorpCode(newEnterCode);
        
        String memberCode = (String) session.getAttribute("SCD");
        corpInfo.setMemberCode(memberCode);

        try {
            // 기업 등록 + 회원 등급 변경 트랜잭션 처리
            int result = enterRegisterService.enterRegister(corpInfo);

            if (result == 2) {
            	session.invalidate();
                model.addAttribute("msg", "기업회원으로 전환되었습니다. 다시 로그인 해주세요.");
                model.addAttribute("url", "/login");
            } else {
                model.addAttribute("msg", "회원 등급 전환에 실패했습니다. 다시 시도해주세요.");
                model.addAttribute("url", "/");
            }

        } catch (Exception e) {
        	log.error("기업 회원 등록 중 예외 발생", e);
            model.addAttribute("msg", "처리 중 오류가 발생했습니다. 다시 시도해주세요.");
            model.addAttribute("url", "/");
        }

        return "enter/mypage/alert";
    }
    
    @GetMapping("/enter/checkDuplicate")
    @ResponseBody
    public int checkDuplicate(@RequestParam String type,
                                               @RequestParam String value) {
    	int isDuple = 0;

        if ("corpBrno".equals(type)) {
            isDuple = enterRegisterService.isBrnoDuplicated(value);
        }

        return isDuple;
    
    }
}






