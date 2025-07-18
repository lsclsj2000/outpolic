package outpolic.enter.search.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import outpolic.enter.search.domain.EnterBookmark;
import outpolic.enter.search.mapper.EnterBookmarkMapper;
import outpolic.enter.search.service.EnterBookmarkService;

@Service
@RequiredArgsConstructor
public class EnterBookmarkServiceImpl implements EnterBookmarkService{

	private final EnterBookmarkMapper enterBookmarkMapper;
    private static final String BOOKMARK_PREFIX = "BM_C"; // PK 접두사

    @Override
    @Transactional // 데이터베이스에 쓰는 작업이므로 트랜잭션 처리
    public void addBookmark(String userId, String clCd) {
        // 1. 새로운 PK 생성 (프로젝트 규칙 준수)
        String lastId = enterBookmarkMapper.findLastBookmarkId();
        if (lastId == null) {
            lastId = BOOKMARK_PREFIX + "0000000"; // 첫 데이터인 경우
        }
        
        int numPart = Integer.parseInt(lastId.substring(BOOKMARK_PREFIX.length()));
        String newBmCd = BOOKMARK_PREFIX + String.format("%07d", numPart + 1);

        // 2. DB에 저장할 객체 생성
        EnterBookmark newBookmark = new EnterBookmark();
        newBookmark.setBmCd(newBmCd);
        newBookmark.setMbrCd(userId);
        newBookmark.setClCd(clCd);

        // 3. Mapper 호출
        enterBookmarkMapper.insertBookmark(newBookmark);
    }

    @Override
    @Transactional
    public void deleteBookmark(String userId, String clCd) {
        // 서비스는 단순히 파라미터를 전달하는 역할만 수행
    	enterBookmarkMapper.deleteBookmark(userId, clCd);
    }

}
