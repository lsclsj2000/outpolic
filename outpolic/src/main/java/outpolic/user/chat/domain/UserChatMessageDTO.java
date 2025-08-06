package outpolic.user.chat.domain;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class UserChatMessageDTO {
	private String chmCd;               // 메시지 코드
    private String chrCd;               // 채팅방 코드
    private String mbrCd;               // 발신자 코드
    private String chmType;            // 메시지유형 (text/file)
    private String chmCn;              // 메시지 내용
    private Timestamp chmSentYmdt;     // 보낸시간
    private boolean userTy;				//메시지 발신인
}
