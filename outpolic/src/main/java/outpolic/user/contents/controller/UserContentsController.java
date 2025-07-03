package outpolic.user.contents.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.user.category.domain.Category;
import outpolic.user.category.domain.CategoryGroup;
import outpolic.user.category.service.CategoryService;
import outpolic.user.search.domain.ContentsDetailDTO;
import outpolic.user.search.service.SearchService;


@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserContentsController {
	
	// final 필드들
    private final SearchService searchService;
    private final CategoryService categoryService;
    
    @GetMapping("/contents/{contentsId}")
    public String contentsDetailView(@PathVariable(value = "contentsId") String contentsId, Model model) {
        
        log.info("상세 페이지 요청: ID = {}", contentsId);

        // =========================================================================
        // [최종 수정] 임시 데이터 코드를 삭제하고, 실제 서비스 호출 코드로 교체
        // =========================================================================
        
        // 1. 서비스에 contentsId를 넘겨서 상세 정보를 조회합니다.
        ContentsDetailDTO detailData = searchService.getContentsDetailById(contentsId);
        
        // 2. (중요) 조회된 데이터가 없을 경우에 대한 처리
        if (detailData == null) {
            // 예를 들어, 존재하지 않는 ID로 접근했을 경우
            // 여기서는 간단히 에러 메시지를 모델에 담아 에러 페이지로 보낼 수 있습니다.
            model.addAttribute("errorMessage", "해당 콘텐츠를 찾을 수 없습니다.");
            return "error/404"; // templates/error/404.html 같은 에러 페이지
        }
        
        // 3. 조회된 실제 데이터를 "detail" 이라는 이름으로 모델에 담습니다.
        model.addAttribute("detail", detailData);
        
        // 4. 상세 페이지 뷰를 리턴합니다.
        return "user/contentsParticular/userContentsParticularView";
    }

    // --- 이 메서드가 있는지 다시 한번 확인해주세요 ---
    // 최종 경로는 "/user" + "/products" = "/user/products" 가 됩니다.
    @GetMapping("/products") 
    public String showContentsBySubCategory(@RequestParam("category") String subCategoryCode, Model model) {
        
        String mainCategoryCode = subCategoryCode.substring(0, 3);
        
        addSidebarDataToModel(mainCategoryCode, model);

        // TODO: 소분류에 해당하는 상품 목록을 가져오는 로직 추가
        
        return "user/contents/userContentsView";
    }
    
    // --- 헬퍼 메서드 ---
    private void addSidebarDataToModel(String mainCategoryCode, Model model) {
        // 인터페이스에 정의된 올바른 메서드 이름으로 호출
        List<CategoryGroup> categoryGroups = categoryService.getCategoryHierarchy(mainCategoryCode);
        Category mainCategory = categoryService.getMainCategory(mainCategoryCode);
        
        model.addAttribute("categoryGroups", categoryGroups);
        model.addAttribute("mainCategory", mainCategory);
    }

}
