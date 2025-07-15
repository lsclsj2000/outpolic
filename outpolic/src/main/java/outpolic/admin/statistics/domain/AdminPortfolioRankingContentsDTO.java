package outpolic.admin.statistics.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AdminPortfolioRankingContentsDTO {

	private String prtfCode;
	private String entCode;
	private String entName;
	private String prtfTtl;
	private String prtfDate;
	private String ctgryName;
	private String stcCode;
	private int rankingView;
	private LocalDateTime rkDate;
}
