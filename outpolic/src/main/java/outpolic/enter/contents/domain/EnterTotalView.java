package outpolic.enter.contents.domain;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class EnterTotalView {
	private String tvCd;
    private String clCd;
    private int tvViewCnt;
    private LocalDateTime tvUpdtYmdt;
}
