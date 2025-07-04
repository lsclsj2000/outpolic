package outpolic.enter.outsourcing.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;
import outpolic.enter.POAddtional.domain.CategorySearchDto;

@Data
public class EnterOutsourcing {
	private String osCd,entCd,ctgryId,osTtl,osExpln,mbrCd,stcCd,osThumbnailUrl;
	private int osFlfmtCnt;
	private LocalDateTime osRegYmdt,osMdfcnYmdt,osStrtYmdt,osEndYmdt;
	private BigDecimal os_amt;
	
	private List<CategorySearchDto> categories;
	private List<String> tagNames;
	
		
	}
	
	
	

