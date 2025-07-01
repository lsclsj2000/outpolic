package outpolic.user.search.domain;

import java.time.LocalDateTime;
import lombok.Data;

@Data // Lombok 어노테이션으로 Getter, Setter 등을 자동으로 생성합니다.
public class ContentsDetailDTO {
    // --- XML의 resultMap과 일치하는 필드들 ---
    private String contentsId;
    private String contentsType;
    private String contentsTitle;
    private String enterName;
    private LocalDateTime registrationDate;
    
    // --- 상세 정보 필드들 ---
    private String contentsBody;
    private int price;
    
    // --- 나중에 추가할 수 있는 필드들 (필요 없다면 지워도 됩니다) ---
    // private double starRating;
    // private int reviewCount;
    // private String enterProfileImg;
}
