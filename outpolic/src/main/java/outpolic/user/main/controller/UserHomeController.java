package outpolic.user.main.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.user.category.domain.UserCategory;
import outpolic.user.category.service.UserCategoryService;
import outpolic.user.ranking.domain.UserPortfolioRankingContentsDTO;
import outpolic.user.ranking.domain.UserRankingContentsDTO;
import outpolic.user.ranking.service.UserRankingService;

@Controller
@Slf4j
@RequestMapping(value="/")
@RequiredArgsConstructor
public class UserHomeController {
	
	 private final UserCategoryService categoryService;
	 
	 private final UserRankingService userRankingService;
	 
	 

    @GetMapping("/") // 메인 페이지 URL
    public String mainPage(HttpSession session, Model model) {
    	
    	//main세션에 등급코드를 담는코드
    	String gred = (String) session.getAttribute("SGrd");
        model.addAttribute("SGrd", gred);

    	// ★★ 1. 세션에서 현재 로그인한 사용자의 ID를 가져옵니다. ★★
        String userId = (String) session.getAttribute("SCD"); // 비로그인 시 null이 됩니다.

        // 1. 서비스 호출: DB에서 대분류 카테고리 목록을 가져옵니다.
        List<UserCategory> mainCategories = categoryService.getMainCategoryList();
        
        // 2. Model에 담기: "mainCategories" 라는 이름으로 HTML에 전달합니다.
        model.addAttribute("mainCategories", mainCategories);
        log.info("메인 페이지 세션 확인: {}", session.getAttribute("SID"));

        // ★★ 2. 서비스 호출 시, 가져온 userId를 파라미터로 전달합니다. ★★
        List<UserPortfolioRankingContentsDTO> popularPortfolioList = userRankingService.getUserRankingPoContents(userId);
        
        model.addAttribute("findPOList", popularPortfolioList);
         
        // 인기 외주 리스트 불러오기 (외주 목록도 찜 기능이 있다면 동일하게 userId를 전달해야 합니다)
        List<UserRankingContentsDTO> popularOutsourcingList = userRankingService.getRankingContentsList(userId);
        
        model.addAttribute("findOSList", popularOutsourcingList);
		return "main";
    }
    

    /**
     * "/userListpage" 페이지 요청 처리 
     */
    @GetMapping("/userListpage")
    public String userListpageView() {

        return "user/main/userListpageView";
    }
    
    @GetMapping("/projectInfo")
    public String projectMainInfo() {
    	return "projectInfo";
    }
}
