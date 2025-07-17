package outpolic.admin.statistics.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AdminViewStatDTO {

	private String clCd;
    private String entName;
    private String ctgryName;
    private long viewCount;
    private LocalDateTime regDate;

    // 포트폴리오 전용
    private String prtfCode;
    private String prtfTtl;

    // 외주 전용
    private String osCode;
    private String osTtl;
}
