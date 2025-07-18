package outpolic.user.company.domain;

import java.util.List;

import lombok.Data;

@Data
public class UserCompanyProfileDTO {

	private String entCd;
    private String entNm;
    private String entExpln;
    private int entScl;
    
    // 계산 또는 다른 테이블에서 가져올 정보
    private double avgReviewScore;
    private int reviewCount;
    private List<String> categories; // 카테고리 이름 목록
}
