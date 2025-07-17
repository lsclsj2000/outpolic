package outpolic.admin.category.service;

import java.util.List;

import outpolic.admin.category.domain.AdminCategory;
import outpolic.admin.category.domain.AdminCategorySaveRequest;

public interface AdminCategoryService {

	List<AdminCategory> getCategorizedList();
	
	void addCategory(AdminCategorySaveRequest request);

    void updateCategory(AdminCategorySaveRequest request);
    
    void deleteCategory(String ctgryId);
}
