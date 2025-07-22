package outpolic.enter.outsourcing.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
import lombok.NoArgsConstructor;
import outpolic.systems.file.domain.FileMetaData;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OutsourcingFormDataDto {
    // 1단계: 기본 정보
    private String osCd;
    private String entCd;
    private String mbrCd;
    private String osTtl;
    private String osExpln;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime osStrtYmdt;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime osEndYmdt;

    private BigDecimal osAmt;
    private int osFlfmtCnt;
    // 2단계: 카테고리 및 태그
    private List<String> categoryCodes;
    private String tags;
    // 3단계: 파일 정보
    private FileMetaData thumbnailFile;

    // 기타
    private String ctgryId;

    private List<MultipartFile> newBodyImageFiles; // [!code ++]
    private List<String> deletedBodyImageCds;    // [!code ++]
}