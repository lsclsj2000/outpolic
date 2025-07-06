package outpolic.user.main.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import outpolic.user.category.domain.Category;
import outpolic.user.category.service.CategoryService;

@Controller
@RequestMapping(value="/")
@RequiredArgsConstructor
public class UserHomeController {

    private final CategoryService categoryService;

    /**
     * 모든 페이지의 "헤더 프래그먼트"를 위한 데이터 공급자
     */
//    @ModelAttribute("megaMenuCategories")
//    public List<Category> populateMegaMenu() {
//        // 이 메소드는 / , /userListpage 등 모든 요청에 대해 먼저 실행됩니다.
//        return categoryService.getMegaMenuData(); 
//    }

    /**
     * 오직 "/" (메인 페이지)의 "본문"을 위한 데이터 공급자
     */
    @GetMapping("/")
    public String mainPage(Model model) {
        List<Category> mainPageCategories = categoryService.getMainCategoryList();
        model.addAttribute("mainCategories", mainPageCategories);
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
