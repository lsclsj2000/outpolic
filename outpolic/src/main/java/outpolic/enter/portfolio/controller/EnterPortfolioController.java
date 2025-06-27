package outpolic.enter.portfolio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EnterPortfolioController {
	 	@GetMapping("/enterPfList")
	 	public String enterPortfolio() {
	    	return "enter/portfolio/portfolioListView";
	  	}
	    @GetMapping("/enterPfContract")
	    public String enterPfCntract() {
	    	return "enter/portfolio/portfolioContractListView";
	    }
}
