package outpolic.enter.portfolio.domain;

import lombok.Data;
import outpolic.enter.POAddtional.domain.CategorySearchDto;
import outpolic.enter.outsourcing.domain.EnterOutsourcing; // EnterOutsourcing import 추가

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class EnterPortfolio {
    private String prtfCd, entCd, prtfTtl, prtfCn, admCd, stcCd, mbrCd, prtfThumbnailUrl;
    private int prtfDwnld_cnt;
    private LocalDateTime prtfRegYmdt, prtfMdfcnYmdt;
    private String ctgryId;
    private List<CategorySearchDto> categories = new ArrayList<>();
    private List<String> tagNames = new ArrayList<>();
    private List<EnterOutsourcing> linkedOutsourcings = new ArrayList<>(); // 새로 추가: 연결된 외주 목록
}