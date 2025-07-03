package outpolic.user.search.service;

import java.util.List;

import outpolic.user.search.domain.Contents;
import outpolic.user.search.domain.ContentsDetailDTO;


public interface SearchService {
	// 검색 목록 조회
	List<Contents> getContentsList(String keyword);
	
	// 검색된 콘텐츠의 상세정보 
	ContentsDetailDTO getContentsDetailById(String contentsId);
}
