package outpolic.enter.main.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.enter.category.domain.EnterCategory;
import outpolic.enter.category.service.EnterCategoryService;
import outpolic.enter.ranking.domain.EnterPortfolioRankingContentsDTO;
import outpolic.enter.ranking.domain.EnterRankingContentsDTO;
import outpolic.enter.ranking.service.EnterRankingService;
import outpolic.enter.review.dto.EnterReviewMainDTO;
import outpolic.enter.review.service.EnterReviewMainService;


@Controller
@RequestMapping("/enter")
@RequiredArgsConstructor
@Slf4j
public class EnterHomeController {
	
	private final EnterCategoryService categoryService;
	
    private final EnterRankingService enterRankingService;
    
    private final EnterReviewMainService enterReviewMainService;

	@GetMapping("") // 메인 페이지 URL
    public String enterMainPage(HttpSession session, Model model) {
		
		//main세션에 등급코드를 담는코드
    	String gred = (String) session.getAttribute("SGrd");
        model.addAttribute("SGrd", gred);
        

    	// ★★ 1. 세션에서 현재 로그인한 사용자의 ID를 가져옵니다. ★★
        String enterId = (String) session.getAttribute("SCD"); // 비로그인 시 null이 됩니다.

        // 1. 서비스 호출: DB에서 대분류 카테고리 목록을 가져옵니다.
        List<EnterCategory> mainCategories = categoryService.getMainCategoryList();
        
        // 2. Model에 담기: "mainCategories" 라는 이름으로 HTML에 전달합니다.
        model.addAttribute("mainCategories", mainCategories);
        log.info("메인 페이지 세션 확인: {}", session.getAttribute("SID"));

        // ★★ 2. 서비스 호출 시, 가져온 enterId를 파라미터로 전달합니다. ★★
        List<EnterPortfolioRankingContentsDTO> popularPortfolioList = enterRankingService.getEnterRankingPoContents(enterId);
        
        model.addAttribute("findPOList", popularPortfolioList);
         
        // 인기 외주 리스트 불러오기 (외주 목록도 찜 기능이 있다면 동일하게 enterId를 전달해야 합니다)
        List<EnterRankingContentsDTO> popularOutsourcingList = enterRankingService.getRankingContentsList(enterId);
        
        List<EnterReviewMainDTO> recentReviewList = enterReviewMainService.getRecentReviewList();
        // 5. 조회된 결과를 "recentReviews" 라는 이름으로 모델에 추가
        model.addAttribute("recentReviews", recentReviewList);
        
        model.addAttribute("findOSList", popularOutsourcingList);
        if("ENTER".equals(gred)) {
			return "enterMain";
        }else{
        	return "redirect:/";
        }
        
    }
	
    @GetMapping("/enterListpage")
    public String enterListpageView() {
        // 이 메소드 안에는 model.addAttribute가 없지만,
        // @ModelAttribute 덕분에 헤더는 이미 'megaMenuCategories' 데이터를 받은 상태입니다.
        return "enter/main/enterListpageView";
    }
}
