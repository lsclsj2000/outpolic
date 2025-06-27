package outpolic.enter.portfolio.service;

import java.util.List;


import outpolic.enter.portfolio.mapper.EnterPortfolioMapper;

public interface EnterPortfolioService {
	// 포트폴리오 등록
	void addPortfolio(EnterPortfolioMapper portfolio);
	
	// 포트폴리오 조회
	List<EnterPortfolioMapper> getPortfolioList();
	
}
