package outpolic.user.category.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.user.category.domain.UserCategory;
import outpolic.user.category.domain.UserCategoryGroup;
import outpolic.user.category.domain.UserSubCategory;
import outpolic.user.category.mapper.UserCategoryMapper;
import outpolic.user.category.service.UserCategoryService;

@Service("userCategoryService") 
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserCategoryServiceImpl implements UserCategoryService{
	

	private final UserCategoryMapper categoryMapper;
	
	@Override
	public List<UserCategoryGroup> getCategoryHierarchy(String mainCategoryCode) {
        
        List<UserCategory> allCategories = categoryMapper.findAll();

        // 1. 이제 Category 클래스에 getParentCategoryId()가 있으므로 이 코드는 정상 동작합니다.
        Map<String, List<UserCategory>> childrenMap = allCategories.stream()
                .filter(c -> c.getParentCategoryId() != null && !c.getParentCategoryId().isEmpty())
                .collect(Collectors.groupingBy(UserCategory::getParentCategoryId));

        List<UserCategory> level2Categories = childrenMap.getOrDefault(mainCategoryCode, new ArrayList<>());

        // 2. 이 로직은 DTO를 반환하며, 계층 구조를 만드는 가장 효율적인 방법입니다.
        return level2Categories.stream()
        	    .map(level2 -> {
        	        UserCategoryGroup groupDto = new UserCategoryGroup();
        	        groupDto.setId(level2.getCategoryId());
        	        groupDto.setName(level2.getCategoryName());

        	        List<UserSubCategory> finalSubCategories = new ArrayList<>();

        	        // ★ 수정 포인트 1: "전체 보기" 링크를 만들 때, URL을 보내지 않습니다.
        	        // new SubCategory(id, name) 생성자만 호출합니다.
        	        finalSubCategories.add(new UserSubCategory(level2.getCategoryId(), level2.getCategoryName() + " 전체"));

        	        List<UserCategory> level3Categories = childrenMap.getOrDefault(level2.getCategoryId(), new ArrayList<>());
        	        
        	        List<UserSubCategory> actualSubCategories = level3Categories.stream()
        	                .map(level3 -> {
        	                    // ★ 수정 포인트 2: 소분류 링크를 만들 때도 URL을 보내지 않습니다.
        	                    // SubCategory가 알아서 URL을 생성할 겁니다.
        	                    return new UserSubCategory(level3.getCategoryId(), level3.getCategoryName());
        	                })
        	                .collect(Collectors.toList());

        	        finalSubCategories.addAll(actualSubCategories);
        	        groupDto.setSubCategorys(finalSubCategories);
        	        return groupDto;
        	    })
        	    .collect(Collectors.toList());
    }

    @Override
    public UserCategory getMainCategory(String mainCategoryCode) {
        // 3. 이제 CategoryMapper에 findById가 있으므로 이 코드는 정상 동작합니다.
        return categoryMapper.findById(mainCategoryCode);
    }
    
    @Override
    public List<UserCategory> getMainCategoryList() {
        return categoryMapper.findMainCategories();
    }
    
    @Override
    public UserCategory getCategoryByCode(String categoryCode) {
        // 컨트롤러에서 받은 categoryCode를 매퍼의 findById 메소드에 전달하여 DB에서 데이터를 가져옵니다.
        // 1단계에서 확인한 매퍼의 "findById"를 그대로 호출합니다.
        return categoryMapper.findById(categoryCode);
    }
    
    @Override
    public List<UserCategory> getMegaMenuData() {
        // XML에서 모든 계층 구조 조립이 끝나므로, Java에서는 호출만 하면 끝!
        return categoryMapper.findTopLevelCategoriesWithChildren();
    }
	
}
