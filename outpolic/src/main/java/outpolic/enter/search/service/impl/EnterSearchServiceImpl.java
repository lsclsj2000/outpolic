package outpolic.enter.search.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.enter.search.domain.EnterContentItemDTO;
import outpolic.enter.search.domain.EnterContents;
import outpolic.enter.search.domain.EnterContentsDetailDTO;
import outpolic.enter.search.mapper.EnterSearchMapper;
import outpolic.enter.search.service.EnterSearchService;

@Service("enterSearchService")
@Transactional
@RequiredArgsConstructor
@Slf4j
public class EnterSearchServiceImpl implements EnterSearchService{
	
	private final EnterSearchMapper enterSearchMapper;
	
	@Override
	public List<EnterContents> getContentsList(String keyword,String userId) {
log.info("서비스 실행: 키워드 '{}'로 검색을 시작합니다.", keyword);
		
		Map<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("userId", userId); // 로그인하지 않았다면 이 값은 null이 됩니다.
		
		List<EnterContents> contentsList = enterSearchMapper.getContentsList(params);		
		
		log.info("검색 완료: 총 {}개의 결과를 찾았습니다.", contentsList.size());
		return contentsList;
	}

	@Override
	public EnterContentsDetailDTO getContentsDetailById(String contentsId, String userId) {
		Map<String, Object> params = new HashMap<>();
        params.put("contentsId", contentsId);
        params.put("userId", userId);
        return enterSearchMapper.getContentsDetailById(params);
	}

	
	// 콘텐츠 리스트뷰 페이지에서 카테고리별 외주를 불러오기 위한 메소드
	@Override
	public List<EnterContentItemDTO> findContentsByCategoryId(String categoryId,String userId) {
		Map<String, Object> params = new HashMap<>();
	    params.put("categoryId", categoryId);
	    params.put("userId", userId);
		return enterSearchMapper.findContentsByCategoryId(params);
	}
}
