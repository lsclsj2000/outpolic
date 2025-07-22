package outpolic.user.search.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.user.search.domain.UserContentItemDTO;
import outpolic.user.search.domain.UserContents;
import outpolic.user.search.domain.UserContentsDetailDTO;
import outpolic.user.search.mapper.UserSearchMapper;
import outpolic.user.search.service.UserSearchService;

@Service("userSearchService")
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserSearchServiceImpl implements UserSearchService{
	
	private final UserSearchMapper searchMapper;
	
	@Override
	public List<UserContents> getContentsList(String keyword,String userId) {
		log.info("서비스 실행: 키워드 '{}'로 검색을 시작합니다.", keyword);
		
		Map<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("userId", userId); // 로그인하지 않았다면 이 값은 null이 됩니다.
		
		List<UserContents> contentsList = searchMapper.getContentsList(params);		
		
		log.info("검색 완료: 총 {}개의 결과를 찾았습니다.", contentsList.size());
		
		return contentsList;
	}

	@Override
	public UserContentsDetailDTO getContentsDetailById(String contentsId, String userId) {
		Map<String, Object> params = new HashMap<>();
	    params.put("contentsId", contentsId);
	    params.put("userId", userId);
        return searchMapper.getContentsDetailById(params);
	}

	
	// 콘텐츠 리스트뷰 페이지에서 카테고리별 외주를 불러오기 위한 메소드
	@Override
	public List<UserContentItemDTO> findContentsByCategoryId(String categoryId) {
		
		return searchMapper.findContentsByCategoryId(categoryId);
	}
}
