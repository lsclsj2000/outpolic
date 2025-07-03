package outpolic.enter.outsourcing.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import outpolic.enter.portfolio.domain.CategorySearchDto;

public class EnterOutsourcing {
	private String osCd,entCd,ctgryId,osTtl,osExpln,mbrCd,stcCd;
	private int osFlfmtCnt;
	private LocalDateTime osRegYmdt,osMdfcnYmdt,osStrtYmdt,osEndYmdt;
	private BigDecimal os_amt;
	
	private List<CategorySearchDto> categories;
	private List<String> tagNames;
	/*
	private List<FileMetaData> files;
	*/
	
	
	
}
