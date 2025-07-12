package outpolic.user.contents.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class UserPerusalContent {

	private String pcRcdCd;       // PC_RCD_CD (PK)
    
    private String mbrCd;         // MBR_CD (FK)
    
    private String clCd;          // CL_CD (FK)
    
    private LocalDateTime pcYmdt; // PC_YMDT
    
    private String stcCd;         // STC_CD (FK)
	
}
