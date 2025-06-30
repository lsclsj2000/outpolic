package outpolic.user.mypage.service.Impl;

import java.util.Random;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import outpolic.user.mypage.service.SmsService;


@Service
public class SmsServiceImpl implements SmsService {
	
	 private final DefaultMessageService messageService;
	 
	 public SmsServiceImpl() {
	        // 실제 키로 교체하세요
	        this.messageService = NurigoApp.INSTANCE.initialize(
	                "NCSZSPXIWHSVFEFX", "D2LCUIQ0JQSSYBEBGMVRJXB7OGCV1WXB", "https://api.coolsms.co.kr");
	    }
		
	
	@Override
	public String sendVerificationCode(String toPhoneNumber, HttpSession session) {
		// 1. 전화번호 포맷 정리 <- 하이푼 제거, 숫자가 아닌것 제거
		String formattedPhone = toPhoneNumber.replaceAll("[^0-9]", "");
		
		// 2. 인증번호 생성 <- 랜덤한 6자리 숫자를 만들기.
		String authCode = String.valueOf(new Random().nextInt(900000) + 100000);
		
		// 3. 메시지 생성
		Message message = new Message();
		message.setFrom("01068628684"); // 실제 발신번호
		message.setTo(formattedPhone);
		message.setText("인증번호 : [" + authCode + "]");
		
		// 4. 메시지 발송
		this.messageService.sendOne(new SingleMessageSendingRequest(message));
		
		// 5. 인증번호 반환 (ex: 세션에 저장하거나 DB에 저장할 수 있음)
		session.setAttribute("authCode", authCode);
		return authCode;
	}
//	"01075503639"
}
