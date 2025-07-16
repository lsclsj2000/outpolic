package outpolic.user.contents.domain;

import lombok.Data;

@Data
public class UserTodayView {

	private String tdvCd;       // tdv_cd
    private String clCd;        // cl_cd
    private int tdvViewCnt;     // tdv_view_cnt
}
