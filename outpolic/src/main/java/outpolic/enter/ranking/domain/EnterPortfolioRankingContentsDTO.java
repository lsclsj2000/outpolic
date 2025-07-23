package outpolic.enter.ranking.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class EnterPortfolioRankingContentsDTO {

	private String prtfCode;
	private String entCode;
	private String entName;
	private String prtfTtl;
	private String prtfDate;
	private String prtfThumbnailUrl;
	private String ctgryName;
	private String stcCode;
	private String clCd;
	private int rankingView;
	private LocalDateTime rkDate;
	private boolean isBookmarked;
}
