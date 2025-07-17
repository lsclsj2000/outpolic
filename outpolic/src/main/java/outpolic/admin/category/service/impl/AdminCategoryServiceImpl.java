package outpolic.admin.category.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import outpolic.admin.category.domain.AdminCategory;
import outpolic.admin.category.domain.AdminCategorySaveRequest;
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
	
	@Override
    @Transactional // DB에 데이터를 쓰는 작업이므로 트랜잭션 처리
    public void addCategory(AdminCategorySaveRequest request) {
        String newId;
        String upId = request.getCtgryUpId();

        // 1. ID 생성
        if (!StringUtils.hasText(upId)) { // 대분류 추가
            String lastId = mapper.findNextLargeCategoryId(); // 예: "020"
            int nextNum = Integer.parseInt(lastId) + 10;
            newId = String.format("%03d", nextNum); // "030"
        } else { // 하위 분류 추가
            String lastId = mapper.findNextChildCategoryId(upId); // 예: "01004"
            String parentPart = lastId.substring(0, upId.length()); // "010"
            String childPart = lastId.substring(upId.length()); // "04"
            int nextNum = Integer.parseInt(childPart) + 1;
            newId = parentPart + String.format("%02d", nextNum); // "010" + "05" -> "01005"
        }
        
        // 2. DB에 저장할 객체 생성
        AdminCategory newCategory = new AdminCategory();
        newCategory.setCtgryId(newId);
        newCategory.setCtgryNm(request.getCtgryNm());
        newCategory.setCtgryUpId(StringUtils.hasText(upId) ? upId : null); // 빈 문자열 대신 NULL 저장
        newCategory.setAdmCd("ADM_C001"); // 고정값
        newCategory.setCtgryAdmCd("ADM_C001"); // 고정값

        // 3. Mapper 호출
        mapper.insertCategory(newCategory);
    }


	@Override
	public void updateCategory(AdminCategorySaveRequest request) {
		AdminCategory categoryToUpdate = new AdminCategory();
        categoryToUpdate.setCtgryId(request.getCtgryId());
        categoryToUpdate.setCtgryNm(request.getCtgryNm());
        
        mapper.updateCategory(categoryToUpdate);
		
	}

	@Override
	public void deleteCategory(String ctgryId) {
		// 1. 삭제하려는 카테고리에 자식이 있는지 확인합니다.
		int childCount = mapper.countChildrenByParentId(ctgryId);

        // 2. 자식이 한 명이라도 있으면, 예외를 발생시켜 작업을 중단시킵니다.
        if (childCount > 0) {
            throw new RuntimeException("하위 카테고리가 존재하여 삭제할 수 없습니다.");
        }

        // 3. 자식이 없으면, 안전하게 해당 카테고리만 삭제합니다.
        mapper.deleteCategoryById(ctgryId);
		
	}

}
