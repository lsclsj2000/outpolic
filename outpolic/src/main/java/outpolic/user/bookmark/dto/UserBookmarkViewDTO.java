package outpolic.user.bookmark.dto;


import java.time.LocalDateTime;

import lombok.Data;

@Data
public class UserBookmarkViewDTO {
	
	 // 북마크 정보
    private String bmCd;
    private String mbrCd;
    private String clCd;
    private String clType;         // ENTER / PRTF / OS 등
    private LocalDateTime bmRegYmdt;
    
    private String cntdCd;
    
    // 공통 콘텐츠 정보
    private String title;          // 기업명 or 포트폴리오 제목 or 외주 제목
    private String writer;         // 기업명
    private String ctgryNm;        // 카테고리 이름
    private String thumbnailUrl;   // 포폴/외주라면 썸네일

    // 외주일 경우
    private String osCd;
    private String osTtl;
    private String osctgryId;
    private String osAmt;

    // 포트폴리오일 경우
    private String prtfCd;
    private String prtfTtl;
    private int prtfDwnldCnt;

    // 기업일 경우
    private String entCd;
    private String entRprsv;
    private Double entScr;
}
