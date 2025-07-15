package outpolic.admin.statistics.domain;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class AdminRankingContentsDTO {

	private String osCode;
	private int rankingView;
	private String entName;
	private String ctgryName;
	private String osTtl;
	private String osExpln;
	private String stcCode;
	private BigDecimal reviewScore;
	private int osAtm;
}
