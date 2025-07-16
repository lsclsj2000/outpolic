package outpolic.enter.POAddtional.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import outpolic.enter.POAddtional.domain.CategorySearchDto;

@Mapper
public interface CategorySearchMapper {
    List<CategorySearchDto> searchByName(String query);
    
    /** 최상위 카테고리(1차) 목록을 조회합니다. */
    List<CategorySearchDto> findTopLevelCategories();

    /** 특정 부모 카테고리에 속한 하위 카테고리 목록을 조회합니다. */
    List<CategorySearchDto> findSubCategories(@Param("parentId") String parentId);
    
    CategorySearchDto findCategoryById(@Param("ctgryId") String ctgryId);

}