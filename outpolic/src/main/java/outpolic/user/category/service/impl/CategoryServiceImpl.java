package outpolic.user.category.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.user.category.domain.Category;
import outpolic.user.category.domain.CategoryGroup;
import outpolic.user.category.service.CategoryService;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService{
	

	
	
	@Override
	public List<CategoryGroup> getCategoryGroupList(String mainCategoryCode) {
		return null;
	}

	@Override
	public Category getMainCategory(String mainCategoryCode) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
