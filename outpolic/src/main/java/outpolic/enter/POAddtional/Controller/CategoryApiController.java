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
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryApiController {

    private final CategorySearchService categorySearchService;

    @GetMapping
    public ResponseEntity<List<CategorySearchDto>> getCategories(
            @RequestParam(value = "parentId", required = false) String parentId) {
        
        List<CategorySearchDto> categories;
        if (parentId == null || parentId.isEmpty()) {
            // parentId가 없으면 1차 카테고리 조회
            categories = categorySearchService.getTopLevelCategories();
        } else {
            // parentId가 있으면 해당 하위 카테고리 조회
            categories = categorySearchService.getSubCategories(parentId);
        }
        return ResponseEntity.ok(categories);
    }
}