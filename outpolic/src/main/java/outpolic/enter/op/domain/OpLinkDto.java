package outpolic.enter.op.domain;

import lombok.Data;

/**
 * @Data: Lombok 어노테이션으로, Getter/Setter 등을 자동으로 만들어줍니다.
 * '연결된 외주-포트폴리오' 정보를 한 번에 담기 위한 DTO(데이터 그릇)입니다.
 * 여러 테이블의 정보를 조합해서 보여줄 때 주로 사용합니다.
 */
@Data
public class OpLinkDto {
	
	private String opCd; 	// 연결 코드 (outsourcing_portfolio 테이블의 PK)
	private String osCd;	// 외주 코드
	private String osTtl; 	// 외주 제목
	private String prtfCd;	// 포트폴리오 코드
	private String prtfTtl;	//포트폴리오 제목
	private String entCd;	//기업 코드
	

}
