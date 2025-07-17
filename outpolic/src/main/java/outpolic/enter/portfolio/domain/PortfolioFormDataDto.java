package outpolic.enter.portfolio.domain;

import lombok.Data;
import outpolic.systems.file.domain.FileMetaData;
import java.util.List;

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

    // 2단계: 카테고리 및 태그
    private List<String> categoryCodes;
    private String tags;

    // 3단계: 썸네일 파일 정보
    private FileMetaData thumbnailFile;
}