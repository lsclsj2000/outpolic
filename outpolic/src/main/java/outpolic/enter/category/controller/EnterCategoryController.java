package outpolic.enter.category.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.enter.category.domain.EnterCategory;
import outpolic.enter.category.domain.EnterCategoryGroup;
import outpolic.enter.category.service.EnterCategoryService;
import outpolic.enter.search.domain.EnterContentItemDTO;
import outpolic.enter.search.service.EnterSearchService;



@Controller("enterCategoryController") 
@RequiredArgsConstructor
@Slf4j
public class EnterCategoryController {

	private final EnterCategoryService categoryService;
	
    private final EnterSearchService searchService;
    
    
    @GetMapping("/enter/contents/category/{categoryCode}")
    public String showContentsByCategory(@PathVariable String categoryCode, Model model, HttpSession session) {
       
        return renderCategoryPage(categoryCode, model, session);
    }

    /**
     * 쿼리 파라미터로 카테고리 코드를 받는 경우 (예: /enter/products?category=010001)
     */
    @GetMapping("/enter/products")
    public String showContentsBySubCategory(@RequestParam("category") String categoryCode, Model model, HttpSession session) {
        
        return renderCategoryPage(categoryCode, model, session);
    }


    /**
     * [신설] 카테고리 페이지 렌더링을 위한 핵심 로직을 처리하는 공통 메서드
     * @param categoryCode 처리할 카테고리 코드
     * @param model View에 전달할 데이터를 담는 객체
     * @param session 사용자 ID를 가져오기 위한 세션 객체
     * @return 렌더링할 뷰의 이름
     */
    private String renderCategoryPage(String categoryCode, Model model, HttpSession session) {
        
        // 1. 현재 카테고리 정보 조회 및 모델에 추가
        EnterCategory currentCategory = categoryService.getCategoryByCode(categoryCode);
        if (currentCategory == null) {
            log.warn("존재하지 않는 카테고리 코드 '{}'에 대한 접근 시도", categoryCode);
            return "error/404";
        }
        model.addAttribute("currentCategory", currentCategory);

        // 2. 사이드바 데이터 조회 및 모델에 추가 (헬퍼 메서드 활용)
        String mainCategoryCode = categoryCode.substring(0, 3);
        addSidebarDataToModel(mainCategoryCode, model);

        // 3. [핵심] 세션에서 사용자 ID를 가져옵니다.
        String enterId = (String) session.getAttribute("SCD");
        log.info("카테고리 '{}' 페이지 조회. 사용자 ID: {}", categoryCode, enterId);

        // 4. [핵심] 서비스 호출 시, categoryCode와 함께 enterId를 전달합니다.
        List<EnterContentItemDTO> contentsList = searchService.findContentsByCategoryId(categoryCode, enterId);
        model.addAttribute("contentsList", contentsList);

        // 5. 최종 뷰 이름을 반환합니다.
        return "enter/contents/enterContentsView";
    }

    /**
     * 사이드바 데이터를 모델에 추가하는 private 헬퍼 메서드 (기존 코드 유지)
     */
    private void addSidebarDataToModel(String mainCategoryCode, Model model) {
        List<EnterCategoryGroup> categoryGroups = categoryService.getCategoryHierarchy(mainCategoryCode);
        EnterCategory mainCategory = categoryService.getMainCategory(mainCategoryCode);
        
        model.addAttribute("categoryGroups", categoryGroups);
        model.addAttribute("mainCategory", mainCategory);
    }
}

