package outpolic.user.chat.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import outpolic.common.util.StatusTextFormatter;
import lombok.RequiredArgsConstructor;
import outpolic.user.chat.domain.UserChatDTO;
import outpolic.user.chat.domain.UserChatMessageDTO;
import outpolic.user.chat.service.UserChatService;

@Controller
@RequestMapping(value = "/user")
@RequiredArgsConstructor
public class UserChatController {

	private final StatusTextFormatter statusTextFormatter;

	private final UserChatService userChatService;
	private final SimpMessagingTemplate messagingTemplate;
	/*
	 * @GetMapping("/userChatMessage") public String userChatMessageView() { return
	 * "user/chat/userChatMessageView"; }
	 * 
	 * 
	 * @PostMapping("/saveNewChat")
	 * 
	 * @ResponseBody public ResponseEntity<?> saveNewChat(@RequestBody Map<String,
	 * String> dataToSend, HttpSession session) {
	 * 
	 * System.out.println("contentsId : " + dataToSend.get("contentsId"));
	 * System.out.println("contentsType : " + dataToSend.get("contentsType"));
	 * System.out.println("enterCode : " + dataToSend.get("enterCode"));
	 * System.out.println("enterName : " + dataToSend.get("enterName"));
	 * System.out.println("message : " + dataToSend.get("message"));
	 * 
	 * userChatService.saveNewChat(dataToSend, session); // 저장 또는 추가 처리 로직
	 * 
	 * return ResponseEntity.ok().body(Map.of("result", "success")); }
	 */

	@PostMapping("/room")
	public ResponseEntity<UserChatDTO> createChatRoom(@RequestBody UserChatDTO userChatDTO, HttpSession session) {
	    
	    userChatService.createChatRoom(userChatDTO, session);
	    UserChatDTO reDto = userChatService.selectChatRoomsInfo(userChatDTO.getChrCd());
	    System.out.println(reDto);
	    return ResponseEntity.ok(reDto);
	}
	
	@PostMapping("/moveRoom")
	public ResponseEntity<UserChatDTO> moveChatRoom(@RequestBody UserChatDTO userChatDTO) {
		System.out.println(userChatDTO);
	    UserChatDTO reDto = userChatService.selectChatRoomsInfo(userChatDTO.getChrCd());
	    System.out.println(reDto);
	    return ResponseEntity.ok(reDto);
	}

	@PostMapping("/getRooms")
	public ResponseEntity<List<UserChatDTO>> getChatRooms(@RequestBody UserChatDTO userChatDTO, HttpSession session) {
		System.out.println("getRooms");
		return ResponseEntity.ok(userChatService.getChatRoomsByMember(userChatDTO, session));
	}

	@PostMapping("/getMessages")
	public ResponseEntity<List<UserChatMessageDTO>> getChatMessages(@RequestBody UserChatMessageDTO messages, HttpSession session) {
		return ResponseEntity.ok(userChatService.getMessagesByRoom(messages, session));
	}

	@PostMapping("/messages")
	public ResponseEntity<Void> saveMessages(@RequestBody List<UserChatMessageDTO> messages, HttpSession session) {
		userChatService.saveChatMessages(messages, session);
		return ResponseEntity.ok().build();
	}
	
	@MessageMapping("/chat/send")
	public void sendMessage(UserChatMessageDTO message, Message<?> msg, SimpMessageHeaderAccessor headerAccessor) {
		HttpSession session = (HttpSession) headerAccessor.getSessionAttributes().get("HTTP_SESSION");
		
	    // 세션에서 로그인한 회원 코드 가져오기
	    String sessionMemberCode = (session != null) ? (String) session.getAttribute("SCD") : null;
	    System.out.println(sessionMemberCode);
	    System.out.println(message.getMbrCd());
	    // message.getMbrCd()와 비교하여 userTy 설정
	    if (sessionMemberCode != null && sessionMemberCode.equals(message.getMbrCd())) {
	        message.setUserTy(true);
	    } else {
	        message.setUserTy(false);
	    }

	    // Now, send the message to all subscribers
	    messagingTemplate.convertAndSend("/topic/chat/" + message.getChrCd(), message);
	}

}
