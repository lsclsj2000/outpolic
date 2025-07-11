package outpolic.admin.search.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AdminContents {
	private String contentsId;
	private String contentsType;
	private String contentsTitle;
    private String enterName;
    private LocalDateTime  registrationDate;
}
