package outpolic.enter.portfolio.service.impl;
import outpolic.enter.portfolio.domain.CategorySearchDto;
import outpolic.enter.portfolio.mapper.CategorySearchMapper;
import outpolic.enter.portfolio.service.CategorySearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
@RequiredArgsConstructor
public class CategorySearchServiceImpl implements CategorySearchService {
    private final CategorySearchMapper categorySearchMapper;
    @Override
    public List<CategorySearchDto> searchCategoriesByName(String query) {
        return categorySearchMapper.searchByName(query);
    }
}