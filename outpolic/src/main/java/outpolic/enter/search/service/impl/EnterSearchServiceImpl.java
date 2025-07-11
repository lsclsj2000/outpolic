package outpolic.enter.search.service.impl;

import java.util.List;

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
	
	private final EnterSearchMapper searchMapper;
	
	@Override
	public List<EnterContents> getContentsList(String keyword) {
		log.info("서비스 실행: 키워드 '{}'로 검색을 시작합니다.", keyword);
		
		List<EnterContents> contentsList = searchMapper.getContentsList(keyword);
		
		log.info("검색 완료: 총 {}개의 결과를 찾았습니다.", contentsList.size());
		return contentsList;
	}

	@Override
	public EnterContentsDetailDTO getContentsDetailById(String contentsId) {
		log.info("상세 정보 조회 서비스 실행: ID = {}", contentsId);
        // 매퍼에 있는 동일한 이름의 메서드를 호출하여 결과를 반환합니다.
        return searchMapper.getContentsDetailById(contentsId);
	}

	
	// 콘텐츠 리스트뷰 페이지에서 카테고리별 외주를 불러오기 위한 메소드
	@Override
	public List<EnterContentItemDTO> findContentsByCategoryId(String categoryId) {
		
		return searchMapper.findContentsByCategoryId(categoryId);
	}
}
