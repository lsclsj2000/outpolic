package outpolic.user.search.service.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import outpolic.user.search.mapper.UserSearchHistoryMapper;
import outpolic.user.search.service.UserSearchHistoryService;

@Service
@RequiredArgsConstructor
public class UserSearchHistoryServiceImpl implements UserSearchHistoryService{
	
	private final UserSearchHistoryMapper userSearchHistoryMapper;
	
	@Override
	public void addSearchHistory(String mbrCd, String searchTerm) {
	// 검색어가 비어있거나 null이면 저장하지 않음
	    if (searchTerm == null || searchTerm.trim().isEmpty()) {
	        return;
	    }
	
	    // 1. 다음 PK를 DB에서 조회
	    String nextShCd = userSearchHistoryMapper.getNextShCd();
	
	    // 2. Mapper에 전달할 파라미터 준비
	    // searchTerm, mbrCd는 메소드 인자로 받음
	    
	    // 3. Mapper 메소드 호출
	    userSearchHistoryMapper.insertSearchHistory(nextShCd, searchTerm, mbrCd);
	}

}
