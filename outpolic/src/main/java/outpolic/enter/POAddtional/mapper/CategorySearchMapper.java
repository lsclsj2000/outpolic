package outpolic.enter.POAddtional.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import outpolic.enter.POAddtional.domain.CategorySearchDto;

@Mapper
public interface CategorySearchMapper {
    List<CategorySearchDto> searchByName(String query);
}