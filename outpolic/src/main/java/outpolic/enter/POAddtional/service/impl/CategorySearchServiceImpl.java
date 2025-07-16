package outpolic.enter.POAddtional.service.impl;

import java.util.LinkedList;
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
    
    @Override
    public List<CategorySearchDto> getTopLevelCategories() {
        return categorySearchMapper.findTopLevelCategories();
    }

    @Override
    public List<CategorySearchDto> getSubCategories(String parentId) {
        return categorySearchMapper.findSubCategories(parentId);
    }
    
    @Override
    public List<CategorySearchDto> getCategoryPath(String ctgryId) {
        LinkedList<CategorySearchDto> path = new LinkedList<>();
        String currentId = ctgryId;
        while (currentId != null && !currentId.isEmpty()) {
            CategorySearchDto category = categorySearchMapper.findCategoryById(currentId);
            if (category != null) {
                path.addFirst(category);
                currentId = category.getCtgryUpId();
            } else {
                break;
            }
        }
        return path;
    }
}