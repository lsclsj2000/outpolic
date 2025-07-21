package outpolic.user.bookmark.service.Impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import outpolic.user.bookmark.dto.UserBookmarkViewDTO;
import outpolic.user.bookmark.mapper.UserBookmarkMapper;
import outpolic.user.bookmark.service.UserBookmarkService;

@Service
@RequiredArgsConstructor
public class UserBookmarkServiceImpl implements UserBookmarkService {
	
	private final UserBookmarkMapper userBookmarkMapper;

	@Override
	public List<UserBookmarkViewDTO> getBookmarkEiByCode(String memberCode) {
		return userBookmarkMapper.getBookmarkEiByCode(memberCode);
	}

	@Override
	public List<UserBookmarkViewDTO> getBookmarkOsByCode(String memberCode) {
		return userBookmarkMapper.getBookmarkOsByCode(memberCode);
	}

	@Override
	public List<UserBookmarkViewDTO> getBookmarkPoByCode(String memberCode) {
		return userBookmarkMapper.getBookmarkPoByCode(memberCode);
	}

}
