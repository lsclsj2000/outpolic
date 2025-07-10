package outpolic.user.search.service;

import java.util.List;

import outpolic.user.search.domain.UserContentItemDTO;
import outpolic.user.search.domain.UserContents;
import outpolic.user.search.domain.UserContentsDetailDTO;


public interface UserSearchService {
	// 검색 목록 조회
	List<UserContents> getContentsList(String keyword);
	
	// 검색된 콘텐츠의 상세정보 
	UserContentsDetailDTO getContentsDetailById(String contentsId);
	
	List<UserContentItemDTO> findContentsByCategoryId(String categoryId);
}
