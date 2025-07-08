package outpolic.enter.portfolio.domain;

import lombok.Data;
import outpolic.enter.POAddtional.domain.CategorySearchDto;
import outpolic.enter.outsourcing.domain.EnterOutsourcing; // EnterOutsourcing import 추가

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EnterPortfolio {
    private String prtfCd, entCd, prtfTtl, prtfCn, admCd, stcCd, mbrCd, prtfThumbnailUrl;
    private int prtfDwnldCnt;
    private LocalDateTime prtfRegYmdt, prtfMdfcnYmdt;
    
    private List<CategorySearchDto> categories;
    private List<String> tagNames;
    private List<EnterOutsourcing> linkedOutsourcings; // 새로 추가: 연결된 외주 목록
}