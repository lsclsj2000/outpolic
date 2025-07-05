package outpolic.enter.outsourcing.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import outpolic.enter.POAddtional.domain.CategorySearchDto;

import jakarta.validation.constraints.NotBlank; // 추가 임포트
import jakarta.validation.constraints.Min;     // 추가 임포트


@Data
public class EnterOutsourcing {
   // @NotBlank(message = "외주 코드는 필수입니다.") // osCd는 보통 생성 로직에서 채워지지만, 안전을 위해
    private String osCd;

    @NotBlank(message = "기업 코드는 필수입니다.")
    private String entCd;

   // @NotBlank(message = "카테고리 ID는 필수입니다.") // DB NOT NULL이므로
    private String ctgryId;

    @NotBlank(message = "외주 프로젝트 제목은 필수입니다.")
    private String osTtl;

    @NotBlank(message = "외주 프로젝트 내용은 필수입니다.")
    private String osExpln;

    @NotBlank(message = "등록자 코드는 필수입니다.")
    private String mbrCd;

    private String admCd; // admCd는 수정 시에만 채워지므로 @NotBlank는 안 붙일 수 있음 (비즈니스 로직에 따라 다름)

   // @NotBlank(message = "상태 코드는 필수입니다.")
    private String stcCd;

    @NotNull(message = "필요 수행 인원은 필수입니다.")
    @Min(value = 1, message = "필요 수행 인원은 최소 1명 이상이어야 합니다.")
    private int osFlfmtCnt;

    //@NotNull(message = "등록일시는 필수입니다.") // DB에서 NOW()로 채워지더라도 유효성 검사 목적
    private LocalDateTime osRegYmdt;
    private LocalDateTime osMdfcnYmdt; // 수정일시는 NULL 허용

    @NotNull(message = "희망 작업 시작일은 필수입니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime osStrtYmdt;

    @NotNull(message = "희망 작업 종료일은 필수입니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime osEndYmdt;

    @NotNull(message = "희망 금액은 필수입니다.")
    @Min(value = 0, message = "희망 금액은 0원 이상이어야 합니다.")
    private BigDecimal osAmt;

    private List<CategorySearchDto> categories; // 백엔드에서 가져올 데이터
    private List<String> tagNames; // 백엔드에서 가져올 데이터
}
	
	
	

