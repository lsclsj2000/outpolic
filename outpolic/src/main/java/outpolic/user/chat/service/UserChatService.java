package outpolic.user.chat.service;

import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;
import outpolic.common.domain.Member;
import outpolic.user.chat.domain.UserChatDTO;
import outpolic.user.chat.domain.UserChatMessageDTO;

public interface UserChatService {
	/*
	 * // 채팅방 목록 조회 List<UserChatDTO> getUserChatRoomList(); //채티방 존재여부
	 * 
	 * //기업회원 정보 UserChatEnterMemberDTO getEnterMemberInfo(String enterCd,
	 * HttpSession session);
	 * 
	 * //채팅방 생성 int saveNewChat(Map<String, String> dataToSend, HttpSession
	 * session); //메시지 저장
	 * 
	 * //메시지 불러오기
	 */
	String getNextChrCd();
	String getNextChmCd();
	void createChatRoom(UserChatDTO dto, HttpSession session);
	UserChatDTO selectChatRoomsInfo(String mbrCd);
    List<UserChatDTO> getChatRoomsByMember(UserChatDTO dto, HttpSession session);
    List<UserChatMessageDTO> getMessagesByRoom(UserChatMessageDTO messages, HttpSession session);
    void saveChatMessages(List<UserChatMessageDTO> messages, HttpSession session);
	
}
