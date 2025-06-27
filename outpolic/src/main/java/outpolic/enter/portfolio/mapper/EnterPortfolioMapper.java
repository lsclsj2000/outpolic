package outpolic.enter.portfolio.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EnterPortfolioMapper {
	
	
	// 포트폴리오 등록
	static int addPortfolio(EnterPortfolioMapper portfolio) {
		// TODO Auto-generated method stub
		return 0;
	}

	// 포트폴리오 조회
	List<EnterPortfolioMapper> getPortfolioList();
}
