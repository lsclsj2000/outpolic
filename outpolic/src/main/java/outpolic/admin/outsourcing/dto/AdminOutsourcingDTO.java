package outpolic.admin.outsourcing.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List; // 필요한 경우 List 타입 필드 추가
import outpolic.systems.file.domain.FileMetaData; // 파일 정보가 필요하다면 임포트

@Data
public class AdminOutsourcingDTO {
    // EnterOutsourcing 도메인과 겹치는 기본 정보
    private String osCd;
    private String entCd;
    private String ctgryId; // 외주 카테고리 ID
    private String osTtl;
    private String osExpln;
    private LocalDateTime osRegYmdt;
    private String mbrCd;
    private LocalDateTime osMdfcnYmdt;
    private String stcCd; // 상태 코드 (SD_ACTIVE 등)
    private LocalDateTime osStrtYmdt;
    private LocalDateTime osEndYmdt;
    private BigDecimal osAmt;
    private int osFlfmtCnt;
    private String osThumbnailUrl; // 외주 썸네일 URL

    // Admin 페이지에 필요한 추가 정보 (예: 카테고리 이름, 파일 정보 일부)
    private String ctgryNm; // 카테고리 이름 (Category 테이블에서 조인하여 가져올 경우)
    private List<FileMetaData> attachedFiles; // 첨부 파일 목록 (이 안에 fileRegYmdt, clCd 등이 포함될 수 있도록)

    // 외주 목록 테이블의 '상태' 컬럼에 표시할 상태 이름
    private String stcNm; // status_code 테이블에서 stc_nm을 가져올 경우
}