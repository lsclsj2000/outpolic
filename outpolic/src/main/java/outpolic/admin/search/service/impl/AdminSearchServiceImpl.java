package outpolic.admin.search.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.admin.search.domain.AdminContentItemDTO;
import outpolic.admin.search.domain.AdminContents;
import outpolic.admin.search.domain.AdminContentsDetailDTO;
import outpolic.admin.search.mapper.AdminSearchMapper;
import outpolic.admin.search.service.AdminSearchService;

@Service("adminSearchService")
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AdminSearchServiceImpl implements AdminSearchService {
private final AdminSearchMapper searchMapper;
	
	@Override
	public List<AdminContents> getContentsList(String keyword) {
		log.info("서비스 실행: 키워드 '{}'로 검색을 시작합니다.", keyword);
		
		List<AdminContents> contentsList = searchMapper.getContentsList(keyword);
		
		log.info("검색 완료: 총 {}개의 결과를 찾았습니다.", contentsList.size());
		return contentsList;
	}

	@Override
	public AdminContentsDetailDTO getContentsDetailById(String contentsId) {
		log.info("상세 정보 조회 서비스 실행: ID = {}", contentsId);
        // 매퍼에 있는 동일한 이름의 메서드를 호출하여 결과를 반환합니다.
        return searchMapper.getContentsDetailById(contentsId);
	}

	
	// 콘텐츠 리스트뷰 페이지에서 카테고리별 외주를 불러오기 위한 메소드
	@Override
	public List<AdminContentItemDTO> findContentsByCategoryId(String categoryId) {
		
		return searchMapper.findContentsByCategoryId(categoryId);
	}
}


