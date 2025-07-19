package outpolic.admin.outsourcing.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class AdminOutsourcingSearchDTO {
	private String searchType;
	private String searchKeyword;
	
	// 기간 검색
	private String dateSearchType;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate startDate;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate endDate;
	
	// 상태 검색 
	private String stcCd;
}
