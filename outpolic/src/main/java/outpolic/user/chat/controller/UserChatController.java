package outpolic.user.chat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping(value="/user")
@RequiredArgsConstructor
public class UserChatController {
	
	
	
	@GetMapping("/userChatMessage")
	public String userChatMessageView() {
		// 채팅 메세지 페이지
		
		return "user/chat/userChatMessageView";
	}
	
	@GetMapping("/userChatRoom")
	public String userChatRoomView() {
		// 채팅방 목록 페이지
		
		return "user/chat/userChatRoomView";
	}
	
	
	
}
