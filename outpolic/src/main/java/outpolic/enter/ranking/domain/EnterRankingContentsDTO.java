package outpolic.enter.ranking.domain;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class EnterRankingContentsDTO {

	private int rankingView;
	private String osCode;
	private String entName;
	private String entCode;
	private String ctgryName;
	private String osTtl;
	private String osExpln;
	private BigDecimal reviewScore;
	private int osAtm;
	
}
