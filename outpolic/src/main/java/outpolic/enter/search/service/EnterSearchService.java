package outpolic.enter.search.service;

import java.util.List;

import outpolic.enter.search.domain.EnterContentItemDTO;
import outpolic.enter.search.domain.EnterContents;
import outpolic.enter.search.domain.EnterContentsDetailDTO;


public interface EnterSearchService {
	// 검색 목록 조회
	List<EnterContents> getContentsList(String keyword);
	
	// 검색된 콘텐츠의 상세정보 
	EnterContentsDetailDTO getContentsDetailById(String contentsId, String userId);
	
	List<EnterContentItemDTO> findContentsByCategoryId(String categoryId);
}
