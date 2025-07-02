package outpolic.user.category.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import outpolic.user.category.domain.Category;

@Mapper
public interface CategoryMapper {

	List<Category> findAll();
	
	Category findById(String categoryId);
	
	List<Category> findMainCategories();
}
