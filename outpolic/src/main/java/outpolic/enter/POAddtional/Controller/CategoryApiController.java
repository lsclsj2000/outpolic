package outpolic.enter.POAddtional.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import outpolic.enter.POAddtional.domain.CategorySearchDto;
import outpolic.enter.POAddtional.service.CategorySearchService;

import java.util.List;

@RestController
@RequestMapping("/api/categories") // 기본 경로
@RequiredArgsConstructor
public class CategoryApiController {

    private final CategorySearchService categorySearchService;

    // 기존: 1차 또는 하위 카테고리 목록 조회 (parentId 기준)
    @GetMapping
    public ResponseEntity<List<CategorySearchDto>> getCategories(
            @RequestParam(value = "parentId", required = false) String parentId) {
        
        List<CategorySearchDto> categories;
        if (parentId == null || parentId.isEmpty()) {
            categories = categorySearchService.getTopLevelCategories();
        } else {
            categories = categorySearchService.getSubCategories(parentId);
        }
        return ResponseEntity.ok(categories);
    }

    // 새로 추가: 검색 쿼리(query)로 카테고리 이름을 조회
    @GetMapping("/search") // <-- 새로운 엔드포인트 추가
    public ResponseEntity<List<CategorySearchDto>> searchCategories(@RequestParam(defaultValue = "") String query) {
        // searchCategoriesByName 서비스 메서드를 사용하여 이름으로 카테고리 검색
        List<CategorySearchDto> categories = categorySearchService.searchCategoriesByName(query);
        return ResponseEntity.ok(categories);
    }
}