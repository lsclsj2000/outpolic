package outpolic.admin.chat.domain;

import lombok.Data;

@Data
public class AdminChat {
	private String ChatRoomCode;
	private String ChatMember1Code;
	private String ChatMember2Code;
	private String ChatRoomName;
	private String ChatRoomRegYmdt;
	private String ChatLastYmdt;
	private String ChatStc1Code;
	private String ChatStc2Code;
	private String ChatStc1Name;
	private String ChatStc2Name;
}
