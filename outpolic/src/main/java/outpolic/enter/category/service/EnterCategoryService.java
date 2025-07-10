package outpolic.enter.category.service;

import java.util.List;

import outpolic.enter.category.domain.EnterCategory;
import outpolic.enter.category.domain.EnterCategoryGroup;

public interface EnterCategoryService {
	
	List<EnterCategoryGroup> getCategoryHierarchy(String mainCategoryCode);
	
	EnterCategory getMainCategory(String mainCategoryCode);
	
	List<EnterCategory> getMainCategoryList();
	
	EnterCategory getCategoryByCode(String categoryCode);
		
	List<EnterCategory> getMegaMenuData();
}
