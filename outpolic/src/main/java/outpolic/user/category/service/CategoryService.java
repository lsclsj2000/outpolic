package outpolic.user.category.service;

import java.util.List;

import outpolic.user.category.domain.Category;
import outpolic.user.category.domain.CategoryGroup;

public interface CategoryService {
	
	List<CategoryGroup> getCategoryHierarchy(String mainCategoryCode);
	
	Category getMainCategory(String mainCategoryCode);
	
	List<Category> getMainCategoryList();
	
}
