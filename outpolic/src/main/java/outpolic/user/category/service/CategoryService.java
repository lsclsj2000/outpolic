package outpolic.user.category.service;

import java.util.List;

import outpolic.user.category.domain.Category;
import outpolic.user.category.domain.CategoryGroup;

public interface CategoryService {

	List<CategoryGroup> getCategoryGroupList(String mainCategoryCode);
	
	Category getMainCategory(String mainCategoryCode);
	
}
