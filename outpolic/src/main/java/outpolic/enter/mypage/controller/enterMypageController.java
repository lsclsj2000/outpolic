package outpolic.enter.mypage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
public class enterMypageController {

	// 기업 마이페이지 
    @GetMapping("/enterMypage")
    public String myPage() {
    	return "enter/mypage/enterMypageView";
    }
    // 기업 개인정보 수정
    @PostMapping("/enterEdit")
    public String userProfileEdit() {
    	return "enter/mypage/enterProfileEditView";
    }
    // 기업 기업소개 페이지
    @GetMapping("/enterEnterpriseInfo")
    public String enterAddData() {
    	return "enter/mypage/enterEnterpriseInfoView";
    }
    // 기업 기업소개 페이지 수정
    @GetMapping("/enterEnterpriseEdit")
    public String enterDataEdit() {
    	return "enter/mypage/enterEnterpriseInfoEditView";
    }
    
    // 살짝 위탁
    @GetMapping("/enterGoodsList")
    public String enterGoodsList() {
    	return "enter/goods/enterGoodsList";
    }
    @GetMapping("/enterPfList")
    public String enterPortfolio() {
    	return "enter/portfolio/portfolioListView";
    }
    @GetMapping("/enterPfContract")
    public String enterPfCntract() {
    	return "enter/portfolio/portfolioContractListView";
    }
    @GetMapping("/outsourcingContractListView")
	public String showOutsourcingPage1() {
		return "/enter/outsourcing/outsourcingContractListView"; // 뷰 이름은 그대로 유지하거나 변경
	}    
    @GetMapping("/outsourcingListView") // URL 경로를 더 구체적으로 변경하여 충돌 방지
	public String outsourcingListView() {
		return "enter/outsourcing/outsourcingListView"; // 뷰 이름은 그대로 유지하거나 변경
	}
    @GetMapping("/outsourcingStatusListView") // URL 경로를 더 구체적으로 변경하여 충돌 방지
	public String showOutsourcingPage2() {
		return "/enter/outsourcing/outsourcingStatusListView"; // 뷰 이름은 그대로 유지하거나 변경
	}

    @GetMapping("/bookMarkListView") // URL 경로를 더 구체적으로 변경하여 충돌 방지
	public String showOutsourcingPage3() {
		return "/enter/bookmark/bookMarkListView"; // 뷰 이름은 그대로 유지하거나 변경
	}
    @GetMapping("/reviewListView") // URL 경로를 더 구체적으로 변경하여 충돌 방지
	public String showOutsourcingPage4() {
		return "/enter/review/reviewListView"; // 뷰 이름은 그대로 유지하거나 변경
	}


    
}
