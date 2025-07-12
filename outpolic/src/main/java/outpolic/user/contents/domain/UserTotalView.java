package outpolic.user.contents.domain;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class UserTotalView {
	private String tvCd;
    private String clCd;
    private int tvViewCnt;
    private LocalDateTime tvUpdtYmdt;
}
