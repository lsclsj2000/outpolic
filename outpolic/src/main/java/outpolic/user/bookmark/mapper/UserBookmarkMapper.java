package outpolic.user.bookmark.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import outpolic.user.bookmark.dto.UserBookmarkViewDTO;

@Mapper
public interface UserBookmarkMapper {
	List<UserBookmarkViewDTO> getBookmarkEiByCode(String memberCode);
	List<UserBookmarkViewDTO> getBookmarkOsByCode(String memberCode);
	List<UserBookmarkViewDTO> getBookmarkPoByCode(String memberCode);
}
