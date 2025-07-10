package outpolic.admin.category.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import outpolic.admin.category.domain.AdminCategory;

@Mapper
public interface AdminCategoryMapper {
	List<AdminCategory> selectAllCategories();
}
