package outpolic.admin.portfolio.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

// Admin 페이지의 포트폴리오 검색 조건을 담을 DTO
@Data
public class AdminPortfolioSearchDTO {
    // 기본 검색
    private String searchType; // prtfTtl, prtfCn, prtfCd, mbrCd, entCd, ctgryId, prtfClient, prtfIndustry
    private String searchKeyword;

    // 기간 검색
    private String dateSearchType; // prtfRegYmdt, prtfMdfcnYmdt, prtfPeriodStart, prtfPeriodEnd
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    // 상태 검색
    private String stcCd; // SD_ACTIVE, SD_INACTIVE, all
}