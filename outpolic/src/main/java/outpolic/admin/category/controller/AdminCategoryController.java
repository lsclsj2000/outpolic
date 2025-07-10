package outpolic.admin.category.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminCategoryController {
	
	@GetMapping("/addCategory")
	public String addCategoryView() {
		return "admin/category/adminCategoryAddView";
	}
	
	@GetMapping("/deleteCategory")
	public String deleteCategoryView() {
		return "admin/category/adminCategoryDeleteView";
	}
	
	@GetMapping("/selectCategory")
	public String selectCategoryView() {
		return "admin/category/adminCategorySelectView";
	}
	
	@GetMapping("/editCategory")
	public String editCategoryView() {
		return "admin/category/adminCategoryEditView";
	}

}
