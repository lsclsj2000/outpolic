package outpolic.user.category.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.user.category.domain.Category;
import outpolic.user.category.domain.CategoryGroup;
import outpolic.user.category.domain.SubCategory;
import outpolic.user.category.mapper.CategoryMapper;
import outpolic.user.category.service.CategoryService;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService{
	

	private final CategoryMapper categoryMapper;
	
	@Override
	public List<CategoryGroup> getCategoryHierarchy(String mainCategoryCode) {
        
        List<Category> allCategories = categoryMapper.findAll();

        // 1. 이제 Category 클래스에 getParentCategoryId()가 있으므로 이 코드는 정상 동작합니다.
        Map<String, List<Category>> childrenMap = allCategories.stream()
                .filter(c -> c.getParentCategoryId() != null && !c.getParentCategoryId().isEmpty())
                .collect(Collectors.groupingBy(Category::getParentCategoryId));

        List<Category> level2Categories = childrenMap.getOrDefault(mainCategoryCode, new ArrayList<>());

        // 2. 이 로직은 DTO를 반환하며, 계층 구조를 만드는 가장 효율적인 방법입니다.
        return level2Categories.stream()
                .map(level2 -> {
                    CategoryGroup groupDto = new CategoryGroup();
                    groupDto.setId(level2.getCategoryId());
                    groupDto.setName(level2.getCategoryName());

                    List<Category> level3Categories = childrenMap.getOrDefault(level2.getCategoryId(), new ArrayList<>());
                    
                    List<SubCategory> subCategory = level3Categories.stream()
                            .map(level3 -> new SubCategory(level3.getCategoryId(), level3.getCategoryName()))
                            .collect(Collectors.toList());

                    groupDto.setSubCategorys(subCategory);
                    return groupDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Category getMainCategory(String mainCategoryCode) {
        // 3. 이제 CategoryMapper에 findById가 있으므로 이 코드는 정상 동작합니다.
        return categoryMapper.findById(mainCategoryCode);
    }
    
    @Override
    public List<Category> getMainCategoryList() {
        return categoryMapper.findMainCategories();
    }

	
}
