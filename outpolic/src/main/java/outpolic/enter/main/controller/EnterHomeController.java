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

@Controller
@RequestMapping("/enter")
@RequiredArgsConstructor
@Slf4j
public class EnterHomeController {
	
	private final EnterCategoryService categoryService;
	
	private final EnterRankingService enterRankingService;

	@GetMapping("") // 메인 페이지 URL
    public String enterMainPage(HttpSession session, Model model) {
        
        // 1. 서비스 호출: DB에서 대분류 카테고리 목록을 가져옵니다.
        List<EnterCategory> mainCategories = categoryService.getMainCategoryList();
        
        // 2. Model에 담기: "mainCategories" 라는 이름으로 HTML에 전달합니다.
        model.addAttribute("mainCategories", mainCategories);
        log.info("메인 페이지 세션 확인: {}", session.getAttribute("SID"));
        
        List<EnterPortfolioRankingContentsDTO> popularPortfolioList = enterRankingService.getEnterRankingPoContents();
        model.addAttribute("findPOList", popularPortfolioList);
        
        // 인기 외주 리스트 불러오기
        List<EnterRankingContentsDTO> popularOutsourcingList = enterRankingService.getRankingContentsList();
        model.addAttribute("findOSList", popularOutsourcingList);

        return "enterMain";
    }
	
    @GetMapping("/enterListpage")
    public String userListpageView() {
        // 이 메소드 안에는 model.addAttribute가 없지만,
        // @ModelAttribute 덕분에 헤더는 이미 'megaMenuCategories' 데이터를 받은 상태입니다.
        return "user/main/userListpageView";
    }
}
