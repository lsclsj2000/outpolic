package outpolic.user.login.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class LoginFailDTO {
	private String lmtCd;           // 제재 코드
	private String mbrCd;           // 회원 코드
	private String lmtDockRsn;      // 제재 사유
	private LocalDateTime lmtStartYmdt; // 시작일
	private LocalDateTime lmtEndYmdt;   // 종료일
	private int lmtRmdDays;         // 남은 일수
}
