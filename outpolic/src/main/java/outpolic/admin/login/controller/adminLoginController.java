package outpolic.admin.login.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import outpolic.admin.login.service.AdminLoginService;
import outpolic.common.domain.Member;
import outpolic.admin.login.DTO.AdminLoginDTO;

@Controller
@RequestMapping(value="/admin")
@RequiredArgsConstructor
public class adminLoginController {
	
	private final AdminLoginService adminLoginService;
	
	@GetMapping("")
	public String adminMain(Model model) {
		return "admin/main/adminMainView";
	}

	
	@GetMapping("/login")
	public String adminLoginView() {
		
		return "admin/login/adminLoginView";
	}
	
	@PostMapping("/login")
	public String adminLogin(@RequestParam String memberId,
				             @RequestParam String memberPw,
				             Member member,
				             HttpSession session,
				             Model model,
				             RedirectAttributes redirectAttributes) {
		Map<String, Object> loginResult = adminLoginService.loginAdmin(memberId, memberPw);
		boolean isMatched = (boolean) loginResult.get("isMatched");
		if(isMatched) {
			AdminLoginDTO adminLoginDTO = (AdminLoginDTO) loginResult.get("adminLoginDTO");
			Member memberInfo = adminLoginDTO.getMemberInfo();
			String grade = memberInfo.getGradeCode();
            
            if("ADMIN".equals(grade)) {
            	
    			session.setAttribute("SID", memberInfo.getMemberId());
                session.setAttribute("SName", memberInfo.getMemberName());
                session.setAttribute("SGrd", memberInfo.getGradeCode());
                session.setAttribute("SCD", memberInfo.getMemberCode());
                session.setAttribute("SACD", adminLoginDTO.getAdminCode());
                
                redirectAttributes.addFlashAttribute("success", "로그인에 성공하였습니다");

                return "redirect:/admin";
            }else {
            	model.addAttribute("msg", "권한이 없는 사용자입니다.");
                model.addAttribute("url", "/");
            	return "user/mypage/alert";
            }
		}else {
        	model.addAttribute("msg", "아이디 혹은 비밀번호가 일치하지 않습니다.");
            model.addAttribute("url", "/admin/login");
        	return "user/mypage/alert";
		}

	}
}

















