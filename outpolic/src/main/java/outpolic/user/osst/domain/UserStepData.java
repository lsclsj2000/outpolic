package outpolic.user.osst.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class UserStepData {
	private String stcCd;
	private String stcNm;
	private String ospSplyYmdt;
	private Integer ospCustYn;
	private String ospCustYmdt;
	private String ospCd;

	private List<UserOsstRecord> reports;
	private List<UserOsstRecord> feedbacks;
	
	private List<UserOsstRecord> finalResults = new ArrayList<>();

	public boolean isCompleted() {
		return ospCustYn != null && ospCustYn == 1;
	}

	public boolean isInProgress() {
		return ospCustYn != null && ospCustYn == 0 && ospSplyYmdt != null;
	}
	
	public List<UserOsstRecord> getAllActivities() {
	    List<UserOsstRecord> all = new ArrayList<>();
	    if (reports != null) all.addAll(reports);
	    if (feedbacks != null) all.addAll(feedbacks);
	    return all;
	}
}
