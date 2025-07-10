package outpolic.user.category.service;

import java.util.List;

import outpolic.user.category.domain.UserCategory;
import outpolic.user.category.domain.UserCategoryGroup;

public interface UserCategoryService {
	
	List<UserCategoryGroup> getCategoryHierarchy(String mainCategoryCode);
	
	UserCategory getMainCategory(String mainCategoryCode);
	
	List<UserCategory> getMainCategoryList();
	
	UserCategory getCategoryByCode(String categoryCode);
		
	List<UserCategory> getMegaMenuData();
}
