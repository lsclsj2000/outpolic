package outpolic.enter.outsourcing.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import outpolic.enter.POAddtional.domain.CategorySearchDto;
import outpolic.enter.portfolio.domain.EnterPortfolio;
import jakarta.validation.constraints.NotBlank; // 추가 임포트
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;     // 추가 임포트

/**
 * @Data: Lombok 라이브러리 어노테이션입니다.
 *  아래 모든 필드에 대해 Getter,Setter,toString 등의 메서드를 자동으로 만들어줍니다.
 *  코드를 매우 간결하게 유지할 수 있게 해주는 편리한 기능입니다.
 */
@Data
/**
 * '외주' 정보를 담는 클래스입니다. (Data Transfer Object, DTO)
 * 	사용자가 입력한 폼 데이터를 담거나, DB 조회 결과를 담는 그릇 역할을 합니다. 
 */
public class EnterOutsourcing {
	// -- 필드(변수) 선언 --
    private String osCd; // 외주코드 (데이터베이스의 PK)
    
    /**
     * @NotBlank: 유효성 검사(Validation) 어노테이션입니다.
     * - 이 필드는 null이 될 수 없고, 공백(" ")만으로 이루어질 수도 없습니다.
     * - message: 유효성 검사에 실패했을 때 보여줄 오류 메세지입니다. 
     */

    @NotBlank(message = "기업 코드는 필수입니다.")
    private String entCd; // 기업 코드 (이 외주를 등록한 작성자)

    private String ctgryId; // 대표 카테고리 ID

    @NotBlank(message = "외주 프로젝트 제목은 필수입니다.")
    private String osTtl;	// 외주 제목

    @NotBlank(message = "외주 프로젝트 내용은 필수입니다.")
    private String osExpln;	// 외주 설명

    @NotBlank(message = "등록자 코드는 필수입니다.")
    private String mbrCd;	// 회원 코드(실제 등록 행위를 한 사람)

    private String admCd; // 수정자(관리자) 코드
    private String stcCd; // 상태 코드 (예: SD_ACTIVE, AD_INACTIVE)
    
    /**
     * @NotNull: 유효성 검사 어노테이션입니다.
     *  - 이 필드는 null이 될 수 없습니다. (주로 숫자, 객체 등에 사용)
     */

    @NotNull(message = "필요 수행 인원은 필수입니다.")
    /**
     *  @Min: 유효성 검사 어노테이션입니다.
     *  -숫자 값이 최소한 value 이상이어야 한다는 규칙입니다.
     */
    @Min(value = 1, message = "필요 수행 인원은 최소 1명 이상이어야 합니다.")
    private int osFlfmtCnt;	// 수행 가능 인원 

    private LocalDateTime osRegYmdt;	// 등록일시 (DB에서 자동 생성)
    private LocalDateTime osMdfcnYmdt; // 수정일시 (DB에서 자동 생성 또는 업데이트)

    @NotNull(message = "희망 작업 시작일은 필수입니다.")
    /**
     * @FutureOrPresent: 유효성 검사 어노테이션입니다.
     * -날짜/ 시간 값이 '현재'이거나 '미래'의 시간이여야 합니다. 과거 시간은 허용하지 않습니다.
     */
    @FutureOrPresent(message="시작일은 현재 또는 미래의 날짜여야 합니다.")
    /**
     * @DateTimeFormat: Spring 프레임워크 어노테이션입니다.
     * -HTML form에서 "yyyy-MM-dd'T'HH:mm" 형태로 넘어온 문자열을
     * Java의 LocalDateTime 객체로 자동으로 변환해주는 편리한 기능입니다. 
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime osStrtYmdt;	// 희망 작업 시작일 

    @NotNull(message = "희망 작업 종료일은 필수입니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime osEndYmdt; // 희망 작업 종료일 

    @NotNull(message = "희망 금액은 필수입니다.")
    @Min(value = 0, message = "희망 금액은 0원 이상이어야 합니다.")
    private BigDecimal osAmt; // 희망 금액 (돈과 같이 정확한 계산이 필요할 때 사용하는 타입)
    
    // --- DB 조회 결과를 담기 위한 추가 필드 ---
    // 이 필드들은 DB의 outsourcing 테이블에 직접 매칭되는 컬럼은 아니지만,
    // Mapper의 ResultMap을 통해 다른 테이블과 JOIN한 결과를 담기 위해 사용됩니다.
    
    private List<CategorySearchDto> categories; // 이 외주에 연결된 카테고리 목록
    private List<String> tagNames; // 이 외주에 연결된 태그 이름 목록
    private List<EnterPortfolio> relatedPortfolios; // 이 외주에 연결된 포트폴리오 목록 
}
	
	
	

