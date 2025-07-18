package outpolic.user.search.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class UserContentItemDTO {

	private String contentsId;
	private String contentsTitle;
	private String enterName;
	private LocalDateTime registrationDate;
	private int price;
	private String clCode;
	private boolean isBookmarked; // 현재 사용자의 찜 여부
}
