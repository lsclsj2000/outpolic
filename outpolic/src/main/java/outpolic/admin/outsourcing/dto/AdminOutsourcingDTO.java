package outpolic.admin.outsourcing.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import outpolic.systems.file.domain.FileMetaData;

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
    private String stcCd;
    private LocalDateTime osStrtYmdt;
    private LocalDateTime osEndYmdt;
    private BigDecimal osAmt;
    private int osFlfmtCnt;
    private String osThumbnailUrl; // 외주 썸네일 URL

    // Admin 페이지에 필요한 추가 정보 (예: 카테고리 이름, 파일 정보 일부)
    private String ctgryNm;
    private List<FileMetaData> attachedFiles;

    // 외주 목록 테이블의 '상태' 컬럼에 표시할 상태 이름
    private String stcNm;
}