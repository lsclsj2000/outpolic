package outpolic.admin.search.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AdminContentsDetailDTO {
	 private String contentsId;
	    private String contentsType;
	    private String contentsTitle;
	    private String enterName;
	    private LocalDateTime registrationDate;
	    
	    // --- 상세 정보 필드들 ---
	    private String contentsBody;
	    private int price;
	    	

}
