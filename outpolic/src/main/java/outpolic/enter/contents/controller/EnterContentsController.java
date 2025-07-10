package outpolic.enter.contents.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.enter.category.domain.EnterCategory;
import outpolic.enter.category.domain.EnterCategoryGroup;
import outpolic.enter.category.service.EnterCategoryService;
import outpolic.enter.search.domain.EnterContentItemDTO;
import outpolic.enter.search.domain.EnterContentsDetailDTO;
import outpolic.enter.search.service.EnterSearchService;


@Controller
@RequestMapping("/enter")
@RequiredArgsConstructor
@Slf4j
public class EnterContentsController {
	
	// final 필드들
    private final EnterSearchService searchService;
    private final EnterCategoryService categoryService;
    
    @GetMapping("/api/contents/{contentsId}")
    @ResponseBody // 이 어노테이션이 핵심!
    public ResponseEntity<EnterContentsDetailDTO> getContentsDetailForModal(@PathVariable("contentsId") String contentsId) {
        log.info("모달 데이터 API 요청: ID = {}", contentsId);

        // 기존 서비스 메소드를 그대로 재활용합니다.
        EnterContentsDetailDTO detailData = searchService.getContentsDetailById(contentsId);

        if (detailData != null) {
            // 데이터가 있으면, DTO와 함께 200 OK 응답을 보냅니다.
            return ResponseEntity.ok(detailData);
        } else {
            // 데이터가 없으면 404 Not Found 응답을 보냅니다.
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/contents/{contentsId}")
    public String contentsDetailView(@PathVariable(value = "contentsId") String contentsId, Model model) {
    	
        
        log.info("상세 페이지 요청: ID = {}", contentsId);


        // 1. 서비스에 contentsId를 넘겨서 상세 정보를 조회합니다.
        EnterContentsDetailDTO detailData = searchService.getContentsDetailById(contentsId);

        // 2. (중요) 조회된 데이터가 없을 경우에 대한 처리
        if (detailData == null) {
            model.addAttribute("errorMessage", "해당 콘텐츠를 찾을 수 없습니다.");
            return "error/404"; // templates/error/404.html 같은 에러 페이지
        }

        // 3. 조회된 실제 데이터를 "detail" 이라는 이름으로 모델에 담습니다.
        model.addAttribute("detail", detailData);

        // 4. 상세 페이지 뷰를 리턴합니다.
        return "enter/contentsParticular/enterContentsParticularView";
    }


    @GetMapping("/products") 
    public String showContentsBySubCategory(@RequestParam("category") String subCategoryCode, Model model) {
    	
    	log.info("카테고리별 콘텐츠 조회 시작. 요청된 categoryCode: {}", subCategoryCode);
        
        String mainCategoryCode = subCategoryCode.substring(0, 3);
        
        addSidebarDataToModel(mainCategoryCode, model);

        EnterCategory currentCategory = categoryService.getCategoryByCode(subCategoryCode);
        if (currentCategory == null) {
            // 존재하지 않는 카테고리일 경우 예외 처리
            log.warn("존재하지 않는 카테고리 코드입니다: {}", subCategoryCode);
            return "error/404"; 
        }
        // "currentCategory" 이름으로 모델에 추가
        model.addAttribute("currentCategory", currentCategory);


        // --- 3. 실제 콘텐츠 목록 조회 ---
        // 이전에 만드신 SearchService의 메소드를 호출합니다.
        List<EnterContentItemDTO> contentsList = searchService.findContentsByCategoryId(subCategoryCode);
        
        // "contentsList" 이름으로 모델에 추가
        model.addAttribute("contentsList", contentsList);
        
        return "enter/contents/enterContentsView";
    }
    
    // --- 헬퍼 메서드 ---
    private void addSidebarDataToModel(String mainCategoryCode, Model model) {
        // 인터페이스에 정의된 올바른 메서드 이름으로 호출
        List<EnterCategoryGroup> categoryGroups = categoryService.getCategoryHierarchy(mainCategoryCode);
        EnterCategory mainCategory = categoryService.getMainCategory(mainCategoryCode);
        
        model.addAttribute("categoryGroups", categoryGroups);
        model.addAttribute("mainCategory", mainCategory);
    }

}
