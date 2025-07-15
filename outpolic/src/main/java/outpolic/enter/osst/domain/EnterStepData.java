package outpolic.enter.osst.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class EnterStepData {
	private String stcCd;
	private String stcNm;
	private String ospSplyYmdt;
	private Integer ospCustYn;
	private String ospCustYmdt;

	private List<EnterOsstRecord> reports;
	private List<EnterOsstRecord> feedbacks;
	
	private List<EnterOsstRecord> finalResults = new ArrayList<>();

	public boolean isCompleted() {
		return ospCustYn != null && ospCustYn == 1;
	}

	public boolean isInProgress() {
		return ospCustYn != null && ospCustYn == 0 && ospSplyYmdt != null;
	}
}
