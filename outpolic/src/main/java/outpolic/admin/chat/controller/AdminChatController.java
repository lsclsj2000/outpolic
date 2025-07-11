package outpolic.admin.chat.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import outpolic.admin.chat.domain.AdminChat;
import outpolic.admin.chat.service.AdminChatService;

@Controller
@RequiredArgsConstructor
@RequestMapping(value="/admin")
public class AdminChatController {
	
	private final AdminChatService adminChatService;
	
	@GetMapping("/adminChatRoom")
	public String adminChatRoomProcessView(Model model) {
		// 채팅방 목록 조회
		List<AdminChat> adminChatRoomList = adminChatService.getAdminChatRoomList();
		
		model.addAttribute("title", "채팅방 관리");
		model.addAttribute("adminChatRoomList", adminChatRoomList);
		
		return "admin/chat/adminChatRoomView";
	}
	
}
