package outpolic.admin.chat.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import outpolic.admin.chat.domain.AdminChat;

@Mapper
public interface AdminChatMapper {
	// 채팅방 목록 조회
	List<AdminChat> getAdminChatRoomList();
}
