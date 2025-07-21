package outpolic.enter.portfolio.domain;

import lombok.Data;
import outpolic.enter.POAddtional.domain.CategorySearchDto;
import outpolic.enter.outsourcing.domain.EnterOutsourcing;
import outpolic.systems.file.domain.FileMetaData;

import java.time.LocalDate; // LocalDate 임포트 추가
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class EnterPortfolio {
    private String prtfCd, entCd, prtfTtl, prtfCn, admCd, stcCd, mbrCd, prtfThumbnailUrl,clCd;
    private int prtfDwnld_cnt;
    private LocalDateTime prtfRegYmdt, prtfMdfcnYmdt;

    // 추가된 필드들
    private LocalDate prtfPeriodStart; // 참여 기간 시작일
    private LocalDate prtfPeriodEnd;   // 참여 기간 종료일
    private String prtfClient;         // 클라이언트명
    private String prtfIndustry;       // 업종
    private String ctgryNm; 
    private String ctgryId;
    private List<CategorySearchDto> categories = new ArrayList<>();
    private List<String> tagNames = new ArrayList<>();
    private List<EnterOutsourcing> linkedOutsourcings = new ArrayList<>();
    // private EnterPortfolioFile thumbnailDetails; // 이 필드는 사용하지 않습니다.
    
    private List<FileMetaData> bodyImages = new ArrayList<>(); // [!code ++]

}