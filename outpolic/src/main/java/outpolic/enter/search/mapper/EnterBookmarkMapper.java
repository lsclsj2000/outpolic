package outpolic.enter.search.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import outpolic.enter.search.domain.EnterBookmark;

@Mapper
public interface EnterBookmarkMapper {

	/**
     * 가장 마지막에 생성된 북마크 ID를 조회합니다.
     * @return 마지막 bm_cd (예: "BM_C0000005")
     */
    String findLastBookmarkId();

    /**
     * 새로운 북마크 정보를 테이블에 삽입합니다.
     * @param bookmark 새로 생성된 북마크 객체
     */
    void insertBookmark(EnterBookmark bookmark);

    /**
     * 사용자의 특정 북마크를 삭제합니다.
     * @param userId 사용자 ID
     * @param clCd 콘텐츠 ID
     */
    void deleteBookmark(@Param("userId") String userId, @Param("clCd") String clCd);
}