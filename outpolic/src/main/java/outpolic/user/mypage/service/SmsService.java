package outpolic.user.mypage.service;

import jakarta.servlet.http.HttpSession;

public interface SmsService {
	String sendVerificationCode(String toPhoneNumber, HttpSession session);
}
