package outpolic.admin.portfolio.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// Admin 페이지에 포트폴리오 목록을 표시하기 위한 DTO
@Data
public class AdminPortfolioDTO {
    // EnterPortfolio 도메인과 겹치는 기본 정보
    private String prtfCd;
    private String entCd;
    private String prtfTtl;
    private String prtfCn;
    private int prtfDwnld_cnt;
    private LocalDateTime prtfRegYmdt;
    private LocalDateTime prtfMdfcnYmdt;
    private String stcCd; // 상태 코드 (SD_ACTIVE 등)
    private String mbrCd;
    private String prtfThumbnailUrl;

    // EnterPortfolio에 추가되었던 필드
    private LocalDate prtfPeriodStart;
    private LocalDate prtfPeriodEnd;
    private String prtfClient;
    private String prtfIndustry;
    private String ctgryId; // 대표 카테고리 ID

    // Admin 페이지에 필요한 추가 정보 (JOIN을 통해 가져올 정보)
    private String ctgryNm; // 카테고리 이름 (category 테이블에서 조인)
    private String stcNm;   // 상태 이름 (status_code 테이블에서 조인)
    // private List<FileMetaData> attachedFiles; // 필요 시 파일 목록 추가
}