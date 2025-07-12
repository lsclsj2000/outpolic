package outpolic.enter.outsourcing.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import outpolic.enter.POAddtional.domain.CategorySearchDto;
import outpolic.enter.portfolio.domain.EnterPortfolio;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;

@Data
public class EnterOutsourcing {
    private String osCd;
    
    @NotBlank(message = "기업 코드는 필수입니다.")
    private String entCd;

    private String ctgryId;

    @NotBlank(message = "외주 프로젝트 제목은 필수입니다.")
    private String osTtl;

    @NotBlank(message = "외주 프로젝트 내용은 필수입니다.")
    private String osExpln;

    @NotBlank(message = "등록자 코드는 필수입니다.")
    private String mbrCd;

    private String admCd;
    private String stcCd;
    
    @NotNull(message = "필요 수행 인원은 필수입니다.")
    @Min(value = 1, message = "필요 수행 인원은 최소 1명 이상이어야 합니다.")
    private int osFlfmtCnt;

    private LocalDateTime osRegYmdt;
    private LocalDateTime osMdfcnYmdt;

    @NotNull(message = "희망 작업 시작일은 필수입니다.")
    @FutureOrPresent(message="시작일은 현재 또는 미래의 날짜여야 합니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime osStrtYmdt;

    @NotNull(message = "희망 작업 종료일은 필수입니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime osEndYmdt;

    @NotNull(message = "희망 금액은 필수입니다.")
    @Min(value = 0, message = "희망 금액은 0원 이상이어야 합니다.")
    private BigDecimal osAmt;
    
    // DB 조회 결과를 담기 위한 추가 필드
    private List<CategorySearchDto> categories = new ArrayList<>();
    private List<String> tagNames = new ArrayList<>();
    private List<EnterPortfolio> relatedPortfolios = new ArrayList<>();
    
    private String prtfThumbnailUrl;;
    
    private String clCd;

}