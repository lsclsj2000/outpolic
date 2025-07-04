package outpolic.enter.POAddtional.service;

import java.util.List;

import outpolic.enter.POAddtional.domain.CategorySearchDto;

public interface CategorySearchService {
    List<CategorySearchDto> searchCategoriesByName(String query);
}