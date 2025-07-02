package outpolic.enter.portfolio.domain;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class EnterPortfolio {
    private String prtfCd, entCd, prtfTtl, prtfCn, admCd, stcCd, mbrCd, prtfThumbnailUrl;
    private int prtfDwnldCnt;
    private LocalDateTime prtfRegYmdt, prtfMdfcnYmdt;
    
    // DB 조회 시 채워질 필드
    private List<CategorySearchDto> categories;
    private List<String> tagNames;
}