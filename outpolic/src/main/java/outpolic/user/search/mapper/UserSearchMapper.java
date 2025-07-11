package outpolic.user.search.mapper;


import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import outpolic.user.search.domain.UserContentItemDTO;
import outpolic.user.search.domain.UserContents;
import outpolic.user.search.domain.UserContentsDetailDTO;


@Mapper
public interface UserSearchMapper {
	/**
	 * 포트폴리오와 외주를 통합하여 키워드로 검색하는 메서드
	 * @param keyword 검색할 키워드
	 * @return 검색결과 리스트
	 */
	List<UserContents> getContentsList(@Param("keyword") String keyword);
	
	// 콘텐츠 상세정보 
	UserContentsDetailDTO getContentsDetailById(String contentsId);
	
	List<UserContentItemDTO> findContentsByCategoryId(String categoryId);
}
