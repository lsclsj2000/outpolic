package outpolic.user.search.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ContentItemDTO {

	private String contentsId;
	private String contentsTitle;
	private String enterName;
	private LocalDateTime registrationDate;
	private int price;
	
}
