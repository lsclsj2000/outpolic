package outpolic.admin.chat.service;

import java.util.List;

import outpolic.admin.chat.domain.AdminChat;

public interface AdminChatService {
	// 채팅방 목록 조회
	List<AdminChat> getAdminChatRoomList();
}
