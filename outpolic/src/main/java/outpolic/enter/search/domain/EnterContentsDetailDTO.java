package outpolic.enter.search.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class EnterContentsDetailDTO {
    // --- XML의 resultMap과 일치하는 필드들 ---
    private String contentsId;
    private String contentsType;
    private String contentsTitle;
    private String enterName;
    private String enterCode;
    private LocalDateTime registrationDate;
    private String clCd;
    
    // --- 상세 정보 필드들 ---
    private String contentsBody;
    private int price;
    
    private LocalDate participationStartDate; // 참여 시작일 (prtf_period_start)
    private LocalDate participationEndDate;   // 참여 종료일 (prtf_period_end)
    private String client;                    // 클라이언트 (prtf_client)
    private String industry;                  // 업종 (prtf_industry)
    private List<String> tags;
    
}
