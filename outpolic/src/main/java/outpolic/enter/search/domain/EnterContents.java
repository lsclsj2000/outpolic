package outpolic.enter.search.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class EnterContents {
	private String contentsId;
	private String contentsType;
	private String contentsTitle;
    private String enterName;
    private LocalDateTime  registrationDate;
    private String clCd;
	private String ctgryName;
	private String osAmt;
    private Double reviewScore;
    private String thumbnailUrl;
    private String isBookmarked;
}
