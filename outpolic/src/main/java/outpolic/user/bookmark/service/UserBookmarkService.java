package outpolic.user.bookmark.service;

import java.util.List;

import org.springframework.stereotype.Service;

import kotlinx.serialization.descriptors.StructureKind.LIST;
import outpolic.user.bookmark.dto.UserBookmarkViewDTO;

@Service
public interface UserBookmarkService {
	List <UserBookmarkViewDTO> getBookmarkEiByCode(String memberCode);
	List <UserBookmarkViewDTO> getBookmarkOsByCode(String memberCode);
	List <UserBookmarkViewDTO> getBookmarkPoByCode(String memberCode);
}
