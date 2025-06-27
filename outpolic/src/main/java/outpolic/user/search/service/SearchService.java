package outpolic.user.search.service;

import java.util.List;

import outpolic.user.search.domain.Contents;


public interface SearchService {
	// 검색 목록 조회
	List<Contents> getContentsList(String keyword);
}
