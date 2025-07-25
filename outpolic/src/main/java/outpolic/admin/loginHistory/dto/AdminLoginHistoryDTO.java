package outpolic.admin.loginHistory.dto;

import java.time.LocalDateTime;

import lombok.Data;
@Data
public class AdminLoginHistoryDTO {
	private String loginCode;     // lh_cd
    private String memberCode;    // mbr_cd
    private LocalDateTime loginTime;
    private LocalDateTime logoutTime;
    
    private String ip;            // lh_ip
    private String adminCode;     // adm_cd
    private LocalDateTime modifiedTime;  // lh_mdfcn_ymdt

}
