package outpolic.admin.osst.domain;

import lombok.Data;

@Data
public class AdminOsst {
	private String ocdCode;
	private String ocdTitle;
	private String mbrCode;
	private String mbrName;
	private String entCode;
	private String entName;
	private String mbrEntName;
	private String stcCode;
	private String stcName;
	private Integer ospCustYn;
	private String ospSplyYmdt;
	private String ospCustYmdt;
}
