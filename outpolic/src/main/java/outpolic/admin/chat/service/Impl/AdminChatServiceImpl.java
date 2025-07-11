package outpolic.admin.chat.service.Impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import outpolic.admin.chat.domain.AdminChat;
import outpolic.admin.chat.mapper.AdminChatMapper;
import outpolic.admin.chat.service.AdminChatService;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminChatServiceImpl implements AdminChatService {
	
	private final AdminChatMapper adminChatMapper;
	
	@Override
	public List<AdminChat> getAdminChatRoomList() {
		// 채팅방 목록 조회
		List<AdminChat> adminChatRoomList = adminChatMapper.getAdminChatRoomList();
		return adminChatRoomList;
	}

}
