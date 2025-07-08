package outpolic.admin.chat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value="/admin")
public class AdminChatController {
	
	
	@GetMapping("/adminChatMessage")
	public String adminChatMessageView() {
		// 채팅 메세지 조회
		
		return "admin/chat/adminChatMessageView";
	}
	
	@GetMapping("/adminChatRoomProcess")
	public String adminChatRoomProcessView() {
		// 채팅방 관리
		
		return "admin/chat/adminChatRoomProcessView";
	}
	
}
