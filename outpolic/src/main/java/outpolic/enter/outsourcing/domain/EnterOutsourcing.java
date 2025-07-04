package outpolic.enter.outsourcing.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;
import outpolic.enter.POAddtional.domain.CategorySearchDto;

@Data
public class EnterOutsourcing {
	private String osCd, entCd, ctgryId, osTtl, osExpln, mbrCd, stcCd;
	
    // ★★★ 이 필드를 추가해주세요 ★★★
    private String admCd;

	private int osFlfmtCnt;
	private LocalDateTime osRegYmdt, osMdfcnYmdt, osStrtYmdt, osEndYmdt;
	private BigDecimal osAmt;
	
	private List<CategorySearchDto> categories;
	private List<String> tagNames;
}
	
	
	

