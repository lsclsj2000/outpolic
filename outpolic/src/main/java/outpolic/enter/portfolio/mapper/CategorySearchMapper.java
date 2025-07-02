package outpolic.enter.portfolio.mapper;

import outpolic.enter.portfolio.domain.CategorySearchDto;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface CategorySearchMapper {
    List<CategorySearchDto> searchByName(String query);
}