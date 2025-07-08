package outpolic.user.ranking.domain;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class UserRankingContentsDTO {

	private int rankingView;
	private String osCode;
	private String entName;
	private String ctgryName;
	private String osTtl;
	private String osExpln;
	private BigDecimal reviewScore;
	private int osAtm;
}
