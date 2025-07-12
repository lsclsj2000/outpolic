package outpolic.user.mypage.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import outpolic.user.mypage.service.SmsService;

@RestController
public class UserInfoEditController {

	/*
	 * private final SmsService smsService;
	 * 
	 * 
	 * 
	 * public UserInfoEditController(SmsService smsService) { this.smsService =
	 * smsService; }
	 * 
	 * @PostMapping("/send-code") public ResponseEntity<String>
	 * sendCode(@RequestParam String phone, HttpSession session) { String authCode =
	 * smsService.sendVerificationCode(phone, session); return
	 * ResponseEntity.ok("인증번호 발송 완료"); }
	 * 
	 * @PostMapping("/verify-code") public ResponseEntity<String>
	 * verifyCode(@RequestParam String code, HttpSession session) { String savedCode
	 * = (String) session.getAttribute("authCode");
	 * 
	 * if (savedCode != null && savedCode.equals(code)) { return
	 * ResponseEntity.ok("인증 성공"); } else { return
	 * ResponseEntity.status(401).body("인증 실패"); } }
	 */
}
