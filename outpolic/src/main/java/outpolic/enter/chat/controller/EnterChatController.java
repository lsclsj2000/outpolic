package outpolic.enter.chat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping(value="/enter")
@RequiredArgsConstructor
public class EnterChatController {
	
	
	
	@GetMapping("/enterChatMessage")
	public String enterChatMessageView() {
		// 채팅 메세지 페이지
		
		return "enter/chat/enterChatMessageView";
	}
	
	
}
