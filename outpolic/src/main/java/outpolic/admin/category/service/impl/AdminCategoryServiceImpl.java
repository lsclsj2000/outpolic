package outpolic.admin.category.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import outpolic.admin.category.domain.AdminCategory;
import outpolic.admin.category.mapper.AdminCategoryMapper;
import outpolic.admin.category.service.AdminCategoryService;


@Service
@RequiredArgsConstructor
public class AdminCategoryServiceImpl implements AdminCategoryService {
	
	private final AdminCategoryMapper mapper;

	@Override
	public List<AdminCategory> getCategorizedList() {
		
		List<AdminCategory> flatList = mapper.selectAllCategories();
		
		List<AdminCategory> categorizedList = new ArrayList<>();
		Map<String, AdminCategory> map = new HashMap<>();
		
		// 2-1. DTO 변환 과정 없이, 조회된 AdminCategory 객체를 바로 맵에 저장합니다.
        for (AdminCategory category : flatList) {
            // 자식 리스트를 초기화해주는 것이 안전할 수 있습니다.
            category.setChildren(new ArrayList<>()); 
            map.put(category.getCtgryId(), category);
        }

        // 2-2. 부모-자식 관계를 설정합니다.
        for (AdminCategory category : flatList) {
            // 상위 ID가 없는 경우(대분류)
            if (category.getCtgryUpId() == null || category.getCtgryUpId().isEmpty()) {
                categorizedList.add(category);
            } else {
                // 상위 ID가 있다면, 맵에서 부모를 찾습니다.
                AdminCategory parentCategory = map.get(category.getCtgryUpId());
                if (parentCategory != null) {
                    // 부모의 children 리스트에 현재 객체를 추가합니다.
                    parentCategory.getChildren().add(category);
                }
            }
        }
		
		return categorizedList;
	}

}
