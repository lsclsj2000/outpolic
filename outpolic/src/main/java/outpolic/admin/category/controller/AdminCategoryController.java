package outpolic.admin.category.controller;


import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import outpolic.admin.category.domain.AdminCategory;
import outpolic.admin.category.domain.AdminCategorySaveRequest;
import outpolic.admin.category.service.AdminCategoryService;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminCategoryController {
	
	private final AdminCategoryService adminCategoryService;
	
	
	@PostMapping("/api/category/save")
    @ResponseBody
    public ResponseEntity<String> saveCategory(@RequestBody AdminCategorySaveRequest request) {
        try {
            switch (request.getMode()) {
                case "addLarge":
                case "addChild":
                    adminCategoryService.addCategory(request);
                    return ResponseEntity.ok("카테고리가 성공적으로 추가되었습니다.");
                
                case "edit":
                    adminCategoryService.updateCategory(request);
                    return ResponseEntity.ok("카테고리가 성공적으로 수정되었습니다.");
                
                default:
                    return ResponseEntity.badRequest().body("알 수 없는 요청 모드입니다.");
            }
        } catch (Exception e) {
            // 운영에서는 e.printStackTrace() 대신 로그 라이브러리(slf4j) 사용 권장
            e.printStackTrace(); 
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("작업 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
	
	@DeleteMapping("/api/category/delete/{ctgryId}")
    @ResponseBody
    public ResponseEntity<String> deleteCategory(@PathVariable String ctgryId) {
        try {
            adminCategoryService.deleteCategory(ctgryId);
            return ResponseEntity.ok("카테고리가 성공적으로 삭제되었습니다.");
        } catch (RuntimeException e) { 
        	//  서비스에서 던진 예외를 받습니다.
            // 비즈니스 규칙 위반(자식 존재)은 서버 오류가 아닌 클라이언트 요청 충돌로 처리합니다.
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            // 그 외 예측하지 못한 오류는 서버 오류로 처리합니다.
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("삭제 중 오류가 발생했습니다.");
        }
    }
	
	
	@GetMapping("/selectCategory")
	public String selectCategoryView(Model model) {
		
		List<AdminCategory> categoryList = adminCategoryService.getCategorizedList(); // 변수 타입 변경
		
		model.addAttribute("categoryList", categoryList);
        
		return "admin/category/adminCategorySelectView";
	}
	

}
