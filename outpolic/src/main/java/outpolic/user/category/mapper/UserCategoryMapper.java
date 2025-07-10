package outpolic.user.category.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import outpolic.user.category.domain.UserCategory;
import outpolic.user.search.domain.UserContentItemDTO;

@Mapper
public interface UserCategoryMapper {

	List<UserCategory> findAll();
	
	UserCategory findById(String categoryId);
	
	List<UserCategory> findMainCategories();
	
	List<UserContentItemDTO> findContentsByCategoryId(String categoryId);
	
	List<UserCategory> findTopLevelCategoriesWithChildren();
}
