package outpolic.enter.outsourcing.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor; // 기본 생성자 추가
import outpolic.systems.file.domain.FileMetaData;
import lombok.AllArgsConstructor; // 모든 필드를 포함한 생성자 추가

// 이 DTO는 단계별로 모이는 외주 데이터를 임시로 저장하기 위한 "폼 데이터" DTO입니다.
// 최종 저장될 EnterOutsourcing과는 다를 수 있습니다 (예: MultipartFile 포함 안 함).
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OutsourcingFormDataDto {
    // 1단계: 기본 정보
    private String osCd; // 외주 코드 (생성 후 세션에 저장)
    private String entCd; // 기업 코드
    private String mbrCd; // 등록자 코드
    private String osTtl; // 외주 제목
    private String osExpln; // 외주 설명
    private LocalDateTime osStrtYmdt; // 희망 작업 시작일
    private LocalDateTime osEndYmdt; // 희망 작업 종료일
    private BigDecimal osAmt; // 희망 금액
    private int osFlfmtCnt; // 수행 가능 인원

    // 2단계: 카테고리 및 태그 (ID 또는 이름 목록)
    private List<String> categoryCodes; // 카테고리 ID (또는 이름) 목록
    private String tags; // 태그 (쉼표로 구분된 문자열)
    private String thumbnailUrl;

    // 3단계: 첨부 파일 정보 (업로드된 파일의 URL/ID)
    // 실제 MultipartFile은 여기서 받지 않고, 별도의 API로 업로드 후 URL만 저장
    private String prtfThumbnailUrl; // 대표 이미지 URL (업로드 후 저장)
    private List<String> referenceFileUrls; // 기타 첨부 파일 URL 목록

    // 4단계: 포트폴리오 연결 정보 (최종 DB 저장 시 활용)
    // 이 단계의 정보는 주로 link/unlink API를 통해 직접 DB에 저장되므로, 여기서는 임시 데이터로만 존재.
    // 하지만, 최종 등록 시 어떤 포트폴리오들이 연결되었는지 확인용으로 사용할 수 있음.
    // private List<String> linkedPortfolioCds; // 필요시 추가

    // 기타 (상태 코드 등은 최종 저장 시 설정)
    private String ctgryId; // 대표 카테고리 ID (카테고리 목록에서 첫 번째 것을 사용할 예정)
    private List<FileMetaData> uploadedFiles;
}