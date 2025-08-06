package outpolic.user.chat.service.Impl;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import outpolic.admin.chat.domain.AdminChat;
import outpolic.admin.chat.mapper.AdminChatMapper;
import outpolic.admin.chat.service.AdminChatService;
import outpolic.user.chat.domain.UserChatDTO;
import outpolic.user.chat.domain.UserChatMessageDTO;
import outpolic.user.chat.mapper.UserChatMapper;
import outpolic.user.chat.service.UserChatService;

@Service
@Transactional
@RequiredArgsConstructor
public class UserChatServiceImpl implements UserChatService {
	
	private final UserChatMapper userChatMapper;

	/*
	 * @Override public List<UserChatDTO> getUserChatRoomList() { // TODO
	 * Auto-generated method stub return null; }
	 * 
	 * @Override public UserChatEnterMemberDTO getEnterMemberInfo(String enterCd,
	 * HttpSession session) { return userChatMapper.getEnterMemberInfo(enterCd);
	 * 
	 * }
	 * 
	 * @Override public int saveNewChat(Map<String, String> dataToSend, HttpSession
	 * session) { UserChatEnterMemberDTO enterMemberInfo =
	 * userChatMapper.getEnterMemberInfo(dataToSend.get("enterCode"));
	 * System.out.println(enterMemberInfo); String id = (String)
	 * session.getAttribute("SID"); System.out.println(id); return 0; }
	 */
	
	@Override
    public void createChatRoom(UserChatDTO dto, HttpSession session) {
		String memberCode = (String) session.getAttribute("SCD");
    	dto.setMbr2Cd(memberCode);
		dto.setChrCd(getNextChrCd());
		userChatMapper.insertChatRoom(dto);
    }

    @Override
    public List<UserChatDTO> getChatRoomsByMember(UserChatDTO dto, HttpSession session) {
    	String memberCode = (String) session.getAttribute("SCD");
    	dto.setMbr1Cd(memberCode);
    	dto.setMbr2Cd(memberCode);
        return userChatMapper.selectChatRoomsByMember(dto);
    }

    @Override
    public List<UserChatMessageDTO> getMessagesByRoom(UserChatMessageDTO messages, HttpSession session) {
    	String memberCode = (String) session.getAttribute("SCD");
    	messages.setMbrCd(memberCode);
    	List<UserChatMessageDTO> resultArr = userChatMapper.selectMessagesByRoom(messages);

	    // userTy 설정
	    for (UserChatMessageDTO msg : resultArr) {
	        msg.setUserTy(memberCode != null && memberCode.equals(msg.getMbrCd()));
	    }
    	
        return userChatMapper.selectMessagesByRoom(messages);
    }

    @Override
    public void saveChatMessages(List<UserChatMessageDTO> messages, HttpSession session) {
        for (UserChatMessageDTO message : messages) {
        	message.setChmCd(getNextChmCd());
        	userChatMapper.insertChatMessage(message);
        }
    }

	@Override
	public String getNextChrCd() {
		return userChatMapper.getNextChrCd();
	}
	
	@Override
	public String getNextChmCd() {
		return userChatMapper.getNextChmCd();
	}

	@Override
	public UserChatDTO selectChatRoomsInfo(String mbrCd) {
		return userChatMapper.selectChatRoomsInfo(mbrCd);
	}
}
