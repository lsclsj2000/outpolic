package outpolic.enter.search.mapper;


import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import outpolic.enter.search.domain.EnterContentItemDTO;
import outpolic.enter.search.domain.EnterContents;
import outpolic.enter.search.domain.EnterContentsDetailDTO;


@Mapper
public interface EnterSearchMapper {
	/**
	 * 포트폴리오와 외주를 통합하여 키워드로 검색하는 메서드
	 * @param keyword 검색할 키워드
	 * @return 검색결과 리스트
	 */
	List<EnterContents> getContentsList(@Param("keyword") String keyword);
	
	// 콘텐츠 상세정보 
	EnterContentsDetailDTO getContentsDetailById(Map<String, Object> params);
	
	List<EnterContentItemDTO> findContentsByCategoryId(String categoryId);
}
