package outpolic.admin.login.dto;

import lombok.Data;
import outpolic.common.domain.Member;

@Data
public class AdminLoginDTO {
	private String adminCode;
	
	private String arCd;
	
	private Member memberInfo;
	
	
}
