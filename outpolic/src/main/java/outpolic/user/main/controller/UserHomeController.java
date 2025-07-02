package outpolic.user.main.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import outpolic.user.category.domain.Category;
import outpolic.user.category.service.CategoryService;

@Controller
@RequestMapping(value="/")
@RequiredArgsConstructor
public class UserHomeController {
	
	private final CategoryService categoryService;

    @GetMapping("/") // 메인 페이지 URL
    public String mainPage(Model model) {
        
        // 1. 서비스 호출: DB에서 대분류 카테고리 목록을 가져옵니다.
        List<Category> mainCategories = categoryService.getMainCategoryList();
        
        // 2. Model에 담기: "mainCategories" 라는 이름으로 HTML에 전달합니다.
        model.addAttribute("mainCategories", mainCategories);

        return "main";
    }
	
	@GetMapping("/userListpage")
	public String userListpageView() {
		
		return "user/main/userListpageView";
	}
}
