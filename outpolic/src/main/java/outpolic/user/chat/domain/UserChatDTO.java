package outpolic.user.chat.domain;

import lombok.Data;

@Data
public class UserChatDTO {
	private String chrCd;            // 채팅방 코드
	private String mbr1Cd;           // 참여자1 코드
	private String mbr2Cd;           // 참여자2 코드
	private String chrNm;            // 채팅방 이름
	private String chrRegYmdt;       // 생성일시
	private String chrLastYmdt;      // 마지막 메시지 시각
	private String chrMdfcnMbrCd;    // 수정자
	private String chrMdfcnYmdt;     // 수정일시
	private String stc1Cd;           // 참여자1 삭제여부
	private String stc2Cd;           // 참여자2 삭제여부
	private String chkRoomStatus;
}
