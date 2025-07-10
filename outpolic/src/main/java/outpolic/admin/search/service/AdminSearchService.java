package outpolic.admin.search.service;

import java.util.List;

import outpolic.admin.search.domain.AdminContentItemDTO;
import outpolic.admin.search.domain.AdminContents;
import outpolic.admin.search.domain.AdminContentsDetailDTO;

public interface AdminSearchService {
	// 검색 목록 조회
		List<AdminContents> getContentsList(String keyword);
		
		// 검색된 콘텐츠의 상세정보 
		AdminContentsDetailDTO getContentsDetailById(String contentsId);
		
		List<AdminContentItemDTO> findContentsByCategoryId(String categoryId);
		
		void deleteContent(String contentsId);

}
