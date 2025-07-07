package outpolic.enter.outsourcingRequest.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OutsourcingRequest {
	private String ocdCd; // 요청 코드(PK)
	
	private String ocdReqType; // 요청 타입 ('신청','문의')
	
	@NotBlank(message = "수요자 정보는 필수입니다.")
	private String mbrCd; // 수요자 아이디
	
	@NotBlank(message = "공급자 정보는 필수입니다.")
	private String entCd; // 공급자 아이디
	
	private String clCd; // 콘텐츠 목록 코드
	
	@NotBlank(message = "제목은 필수입니다.")
	private String ocdTtl; //제목
	
	private String ocdFrctnCmdty; //제작물
	
	private String ocdDlvgdsMthd; //납품방법
	
	@NotBlank(message = "상세 설명은 필수입니다.")
	private String ocdExpln; // 추가 설명 
	
  //  @NotNull(message = "희망 작업 기한은 필수입니다.")
    @Future(message = "희망 작업 기한은 미래의 날짜여야 합니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	private LocalDateTime ocdDedline; //희망 작업 기한
	
	private BigDecimal  ocdAmt; //희망 단가
	
	private String stcCd; //상태 코드
	
	private LocalDateTime ocdDmndYmdt; //요청 일시
	private LocalDateTime ocdRspnsYmdt; // 응답 일시
	
	private String mbrCd2; // 수정자 
	private LocalDateTime ocdMdfcnYmdt; //수정일시
	
	private String ocdYn; //신청 취소 여부
	private LocalDateTime ocdYnYmdt; //신청 취소 일시 
	

	
}
