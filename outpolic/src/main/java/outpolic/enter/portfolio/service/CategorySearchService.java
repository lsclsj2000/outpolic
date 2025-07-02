package outpolic.enter.portfolio.service;
import outpolic.enter.portfolio.domain.CategorySearchDto;
import java.util.List;
public interface CategorySearchService {
    List<CategorySearchDto> searchCategoriesByName(String query);
}