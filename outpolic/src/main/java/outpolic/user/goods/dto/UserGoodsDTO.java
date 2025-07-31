package outpolic.user.goods.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;

@Data
public class UserGoodsDTO {

	private String gdsCd;
	private String gdsNm;
	private BigDecimal gdsAmt;
	private String gdsType;
	private int gdsPeriodQuantity;
	private String gdsUnit;
	private String gdsRegAdmCd;
	private Timestamp gdsRegYmdt;
	private String gdsMdfcnAdmCd;
	private Timestamp gdsMdfcnYmdt;
	private int gdsStatus;
	private String gdsTargetMemberType;
}
