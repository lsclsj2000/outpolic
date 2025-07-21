package outpolic.user.passwordChange.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.user.passwordChange.mapper.UserPasswordChangeMapper;
import outpolic.user.passwordChange.service.UserPasswordChangeService;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserPasswordChangeController {
	
	private final UserPasswordChangeMapper userPasswordChangeMapper;
	private final UserPasswordChangeService userPasswordChangeService;
	private final PasswordEncoder passwordEncoder;

    // 비밀번호 재설정 페이지
    @GetMapping("/pswdCng")
    public String userPswdChange() {
        return "user/login/userPswdChangeView"; 
    }
    
    @PostMapping("/user/passwordChange")
    @ResponseBody
    public Map<String, Object> passwordChange(@RequestParam String currentPassword,
                                              @RequestParam String newPassword,
                                              @RequestParam String confirmPassword,
                                              HttpSession session,
                                              Model model) {
        Map<String, Object> response = new HashMap<>();
        String memberCode = (String) session.getAttribute("SCD");
        String gred = (String) session.getAttribute("SGrd");
        model.addAttribute("SGrd", gred);

        if (memberCode == null) {
            response.put("status", "fail");
            response.put("msg", "로그인이 만료되었습니다. 다시 로그인해주세요.");
            return response;
        }

        if (!newPassword.equals(confirmPassword)) {
            response.put("status", "fail");
            response.put("msg", "새 비밀번호와 확인이 일치하지 않습니다.");
            return response;
        }
        if(newPassword.equals(currentPassword)) {
        	response.put("sataus", "fail");
        	response.put("msg", "현재 비밀번호와 같은 비밀번호로는 변경하실 수 없습니다");
        	return response;
        }

        String encodedPw = userPasswordChangeService.getEncodedPassword(memberCode);
        if (!passwordEncoder.matches(currentPassword, encodedPw)) {
            response.put("status", "fail");
            response.put("msg", "현재 비밀번호가 일치하지 않습니다.");
            return response;
        }

        userPasswordChangeService.updatePassword(memberCode, newPassword); 
        session.invalidate();

        response.put("status", "success");
        response.put("msg", "비밀번호가 성공적으로 변경되었습니다.");
        return response;
    }
    
    
}
