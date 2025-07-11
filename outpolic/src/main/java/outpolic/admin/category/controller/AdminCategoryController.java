package outpolic.admin.category.controller;


import java.util.List;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import outpolic.admin.category.domain.AdminCategory;
import outpolic.admin.category.service.AdminCategoryService;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminCategoryController {
	
	private final AdminCategoryService adminCategoryService;
	
	@GetMapping("/addCategory")
	public String addCategoryView() {
		return "admin/category/adminCategoryAddView";
	}
	
	@GetMapping("/deleteCategory")
	public String deleteCategoryView() {
		return "admin/category/adminCategoryeDeleteView";
	}
	
	@GetMapping("/selectCategory")
	public String selectCategoryView(Model model) {
		
		List<AdminCategory> categoryList = adminCategoryService.getCategorizedList(); // 변수 타입 변경
		
		model.addAttribute("categoryList", categoryList);
        
		return "admin/category/adminCategorySelectView";
	}
	
	@GetMapping("/editCategory")
	public String editCategoryView() {
		return "admin/category/adminCategoryEditView";
	}

}
