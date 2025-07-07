package outpolic.user.login.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class LoginFailDTO {
	private String loginFailHistoryCode; 
	private String loginFailIp; 
	private LocalDateTime loginFailYmdt; 
	private int loginFailCount; 
	private LocalDateTime loginBanStartYmdt; 
	private LocalDateTime loginBanEndYmdt; 
	private String loginFailReason; 
}
