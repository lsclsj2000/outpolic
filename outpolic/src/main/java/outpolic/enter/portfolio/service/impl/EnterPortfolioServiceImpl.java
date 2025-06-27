package outpolic.enter.portfolio.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.enter.portfolio.mapper.EnterPortfolioMapper;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class EnterPortfolioServiceImpl {
	private final EnterPortfolioMapper portfolioMapper;
	
	public List<EnterPortfolioMapper> getPortfolioList(){
		return portfolioMapper.getPortfolioList();
	}
	
	public void addPortfolio(EnterPortfolioMapper portfolio) {
		int popori = EnterPortfolioMapper.addPortfolio(portfolio);
		
		String insertPopori = "포트폴리오 등록 실패";
		
		if(popori>0) insertPopori = "포트폴리오등록 성공";
			
			log.info("포트폴리오 등록 성공");
			
	}
}
