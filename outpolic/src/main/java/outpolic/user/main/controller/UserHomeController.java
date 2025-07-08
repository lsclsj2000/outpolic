package outpolic.user.main.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.user.category.domain.Category;
import outpolic.user.category.service.CategoryService;
import outpolic.user.ranking.domain.UserRankingContentsDTO;
import outpolic.user.ranking.service.UserRankingService;

@Controller
@Slf4j
@RequestMapping(value="/")
@RequiredArgsConstructor
public class UserHomeController {
	 private final CategoryService categoryService;
	 private final UserRankingService userRankingService;
	 
	 

    @GetMapping("/") // 메인 페이지 URL
    public String mainPage(HttpSession session, Model model) {
        
        // 1. 서비스 호출: DB에서 대분류 카테고리 목록을 가져옵니다.
        List<Category> mainCategories = categoryService.getMainCategoryList();
        
        // 2. Model에 담기: "mainCategories" 라는 이름으로 HTML에 전달합니다.
        model.addAttribute("mainCategories", mainCategories);
        log.info("메인 페이지 세션 확인: {}", session.getAttribute("SID"));
        
        // 인기 외주 리스트 불러오기
        List<UserRankingContentsDTO> popularOutsourcingList = userRankingService.getRankingContentsList();
        model.addAttribute("findOSList", popularOutsourcingList);

        return "main";
    }

    /**
     * "/userListpage" 페이지 요청 처리 
     */
    @GetMapping("/userListpage")
    public String userListpageView() {
        // 이 메소드 안에는 model.addAttribute가 없지만,
        // @ModelAttribute 덕분에 헤더는 이미 'megaMenuCategories' 데이터를 받은 상태입니다.
        return "user/main/userListpageView";
    }
}
