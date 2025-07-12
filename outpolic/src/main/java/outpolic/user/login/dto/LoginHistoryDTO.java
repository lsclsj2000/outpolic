package outpolic.user.login.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class LoginHistoryDTO {
	private String loginHistoryCode; 
	private String memberCode; 
	private LocalDateTime loginYmdt; 
	private LocalDateTime logoutYmdt; 
	private String loginIp; 
	private String adminCode;
	private LocalDateTime loginHistoryModifiedYmdt;
}
