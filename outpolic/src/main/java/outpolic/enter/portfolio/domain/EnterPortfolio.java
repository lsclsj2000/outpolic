package outpolic.enter.portfolio.domain;

import lombok.Data;
import outpolic.enter.POAddtional.domain.CategorySearchDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EnterPortfolio {
    private String prtfCd, entCd, prtfTtl, prtfCn, admCd, stcCd, mbrCd, prtfThumbnailUrl;
    private int prtfDwnldCnt;
    private LocalDateTime prtfRegYmdt, prtfMdfcnYmdt;
    
    private List<CategorySearchDto> categories;
    private List<String> tagNames;
    
 

}