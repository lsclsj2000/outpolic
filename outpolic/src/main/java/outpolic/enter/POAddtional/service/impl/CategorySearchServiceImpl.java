package outpolic.enter.POAddtional.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import outpolic.enter.POAddtional.domain.CategorySearchDto;
import outpolic.enter.POAddtional.mapper.CategorySearchMapper;
import outpolic.enter.POAddtional.service.CategorySearchService;

@Service
@RequiredArgsConstructor
public class CategorySearchServiceImpl implements CategorySearchService {
    private final CategorySearchMapper categorySearchMapper;
    @Override
    public List<CategorySearchDto> searchCategoriesByName(String query) {
        return categorySearchMapper.searchByName(query);
    }
}