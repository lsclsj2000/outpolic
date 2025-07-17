package outpolic.enter.portfolio.domain;

import lombok.Data;
import outpolic.systems.file.domain.FileMetaData;
import java.time.LocalDate; // LocalDate 임포트 추가
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat; // DateTimeFormat 임포트 추가


// 각 단계별로 데이터를 임시 저장하기 위한 DTO
@Data
public class PortfolioFormDataDto {

    // (자동 생성)
    private String prtfCd;
    private String entCd;
    private String mbrCd;

    // 1단계: 기본 정보
    private String prtfTtl;
    private String prtfCn;

    // 2단계: 참여 기간 및 클라이언트 정보 (새로 추가되는 단계)
    @DateTimeFormat(pattern = "yyyy-MM-dd") // 날짜 형식 지정
    private LocalDate prtfPeriodStart; // 참여 기간 시작일
    @DateTimeFormat(pattern = "yyyy-MM-dd") // 날짜 형식 지정
    private LocalDate prtfPeriodEnd;   // 참여 기간 종료일
    private String prtfClient;         // 클라이언트명
    private String prtfIndustry;       // 업종

    // 3단계: 카테고리 및 태그 (기존 2단계 -> 3단계)
    private List<String> categoryCodes;
    private String tags;

    // 4단계: 썸네일 파일 정보 (기존 3단계 -> 4단계)
    private FileMetaData thumbnailFile;

    // 기타
    private String ctgryId;
}