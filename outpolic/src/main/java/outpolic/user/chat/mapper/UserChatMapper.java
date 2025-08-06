package outpolic.user.chat.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import outpolic.admin.chat.domain.AdminChat;
import outpolic.user.chat.domain.UserChatDTO;
import outpolic.user.chat.domain.UserChatMessageDTO;

@Mapper
public interface UserChatMapper {
	void insertChatRoom(UserChatDTO dto);

	List<UserChatDTO> selectChatRoomsByMember(UserChatDTO dto);

	List<UserChatMessageDTO> selectMessagesByRoom(UserChatMessageDTO dto);

	void insertChatMessage(UserChatMessageDTO dto);

	String getNextChrCd();
	
	String getNextChmCd();

	UserChatDTO selectChatRoomsInfo(String mbrCd);

	/*
	 * // 채팅방 목록 조회 List<UserChatDTO> getUserChatRoomList(); //채티방 존재여부
	 * 
	 * //기업회원 정보 UserChatEnterMemberDTO getEnterMemberInfo(String enterCd);
	 */

}
