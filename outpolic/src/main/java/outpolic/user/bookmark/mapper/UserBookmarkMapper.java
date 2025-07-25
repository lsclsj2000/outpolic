package outpolic.user.bookmark.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import outpolic.user.bookmark.dto.UserBookmarkViewDTO;

@Mapper
public interface UserBookmarkMapper {
	// 기업 북마크
	List<UserBookmarkViewDTO> getBookmarkEiByCode(String memberCode);
	// 외주 북마크
	List<UserBookmarkViewDTO> getBookmarkOsByCode(String memberCode);
	// 포트폴리오 북마크
	List<UserBookmarkViewDTO> getBookmarkPoByCode(String memberCode);
}
