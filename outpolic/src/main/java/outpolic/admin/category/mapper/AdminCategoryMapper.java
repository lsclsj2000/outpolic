package outpolic.admin.category.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import outpolic.admin.category.domain.AdminCategory;
import outpolic.admin.category.domain.AdminCategorySaveRequest;

@Mapper
public interface AdminCategoryMapper {
	
	// ID 생성을 위한 쿼리 메소드들
	List<AdminCategory> selectAllCategories();
	
    String findNextLargeCategoryId();
    
    String findNextChildCategoryId(@Param("parentId") String parentId);
	
    // 파라미터 타입을 AdminCategory로 변경
	void insertCategory(AdminCategory category);

    // 파라미터 타입을 AdminCategory로 변경
    void updateCategory(AdminCategory category);
    
    // 삭제 관련 메소드를 아래 내용으로 교체/추가합니다 
    int countChildrenByParentId(@Param("parentId") String parentId);
    void deleteCategoryById(@Param("ctgryId") String ctgryId);
}
