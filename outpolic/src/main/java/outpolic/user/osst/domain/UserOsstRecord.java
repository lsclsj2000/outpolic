package outpolic.user.osst.domain;

import lombok.Data;

@Data
public class UserOsstRecord {
	private String osstRecordCode;
	private String osstPrograssCode;
	private String osstRecordStcCode;
	private String osstRecordStcName;
	private String osstRecordType;
	private String osstRecordMbrCode;
	private String osstRecordMbrName;
	private String osstRecordTitle;
	private String osstRecordCn;
	private String osstRecordRegYmdt;
	private String osstRecordUpCode;
}