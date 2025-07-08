package outpolic.enter.outsourcingRequest.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

/**
 *  '외주 신청' 폼 데이터를 담는 그릇 역할을 하는 클래스 (DTO)
 */

@Data
public class OutsourcingRequest {
	private String ocdCd;			// 요청코드 (PK)
	private String ocdReqType;		// 요청 타입 ('신청' 또는 '문의')
	private String mbrCd;			// 수요자 아이디 (신청한 사람)
	private String entCd;			// 공급자 아이디 (신청받은 기업)
	private String clCd;			// 콘텐츠 목록 코드 (이 신청 건의 고유 콘텐츠 코드)
	private String ocdTtl;			// 제목
	private String ocdFrctnCmdty;	// 제작물
	private String ocdDlvgdsMthd;	// 납품방법
	private String ocdExpln;		// 추가 설명 
	private LocalDateTime ocdDedline;	// 희망 작업 기한
	private BigDecimal ocdAmt;		// 희망 단가
	private String stcCd;			// 상태 코드 (예: 승인 전, 승인, 거절 등)
	private LocalDateTime ocdDmndYmdt; 	// 요청 일시
	private LocalDateTime ocdRspnsYmdt; // 응답 일시
	private String mbrCd2;			// 수정자
	private LocalDateTime ocdMdfcnYmdt; //수정일시
	private String ocdYn; 				//신청 취소 여부('Y'/'N')
	private LocalDateTime ocdYnYmdt;	// 신청 취소 일시 
}
