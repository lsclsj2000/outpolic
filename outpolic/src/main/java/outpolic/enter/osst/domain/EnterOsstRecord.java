package outpolic.enter.osst.domain;

import lombok.Data;

@Data
public class EnterOsstRecord {
	private String osstDetailCode;
	private String osstDetailMemberCode;
	private String osstDetailMemberName;
	private String osstDetailEntCode;
	private String osstDetailEntName;
	private String osstDetailEntMbrName;
	private String osstDetailTitle;
	private String osstDetailExpln;
	private String osstDetailStcCode;
	private String osstDetailDmndYmdt;
}