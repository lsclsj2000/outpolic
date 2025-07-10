package outpolic.enter.category.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import outpolic.enter.category.domain.EnterCategory;
import outpolic.enter.search.domain.EnterContentItemDTO;

@Mapper
public interface EnterCategoryMapper {

	List<EnterCategory> findAll();
	
	EnterCategory findById(String categoryId);
	
	List<EnterCategory> findMainCategories();
	
	List<EnterContentItemDTO> findContentsByCategoryId(String categoryId);
	
	List<EnterCategory> findTopLevelCategoriesWithChildren();
}
