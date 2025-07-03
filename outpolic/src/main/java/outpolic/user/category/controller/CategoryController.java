package outpolic.user.category.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.user.category.domain.Category;
import outpolic.user.category.domain.CategoryGroup;
import outpolic.user.category.service.CategoryService;
import outpolic.user.search.domain.ContentItemDTO;
import outpolic.user.search.service.SearchService;


@Controller
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;
    private final SearchService searchService;
    
    
    @GetMapping("/contents/category/{categoryCode}")
    public String showContentsByCategory(@PathVariable String categoryCode, Model model) {
    	
    	Category currentCategory = categoryService.getCategoryByCode(categoryCode);
    	if (currentCategory == null) {
            return "error/404"; // 혹은 다른 적절한 처리
        }
    	model.addAttribute("currentCategory", currentCategory);
    	
        // 1. [메인 컨텐츠용 데이터] 현재 카테고리에 속한 콘텐츠 목록 가져오기

        // 현재 소분류 코드(categoryCode)에서 대분류 코드(예: "010")를 추출
        String mainCategoryCode = categoryCode.substring(0, 3); 

        // 2. [사이드바용 데이터] 대분류에 맞는 계층형 카테고리 목록 가져오기
        List<CategoryGroup> categoryGroups = categoryService.getCategoryHierarchy(mainCategoryCode);
        model.addAttribute("categoryGroups", categoryGroups);

        // 3. [사이드바용 데이터] 대분류 정보 가져오기
        Category mainCategory = categoryService.getMainCategory(mainCategoryCode);
        model.addAttribute("mainCategory", mainCategory);
        // --- 사이드바 로직 끝 ---

        // 콘텐츠 목록을 보여줄 뷰 페이지 반환
        return "/user/contents/userContentsView"; 
    }
    
    // --- 2. 소분류 클릭 시 (새로 추가하는 코드) ---
    @GetMapping("/products")
    public String showContentsBySubCategory(@RequestParam("category") String subCategoryCode, Model model) {
        

        // 소분류 코드에서 대분류 코드를 추출 (0100101 -> 010)
        String mainCategoryCode = subCategoryCode.substring(0, 3);
        addSidebarDataToModel(mainCategoryCode, model);

        // 소분류에 해당하는 카테고리 이름을 페이지 타이틀 등으로 사용하기 위해 조회
        Category currentCategory = categoryService.getMainCategory(subCategoryCode);
        
        model.addAttribute("currentCategory", currentCategory);
        
        List<ContentItemDTO> contents = searchService.findContentsByCategoryId(subCategoryCode);
        model.addAttribute("contentsList", contents); // View에서 사용할 이름 "contentsList"
        
        // 대분류/소분류가 공유하는 동일한 뷰 페이지를 반환
        return "user/contents/userContentsView";
    }


    // --- 3. 중복 로직을 처리하는 private 헬퍼 메서드 ---
    private void addSidebarDataToModel(String mainCategoryCode, Model model) {
        List<CategoryGroup> categoryGroups = categoryService.getCategoryHierarchy(mainCategoryCode);
        Category mainCategory = categoryService.getMainCategory(mainCategoryCode);
        
        model.addAttribute("categoryGroups", categoryGroups);
        model.addAttribute("mainCategory", mainCategory);
    }
}

