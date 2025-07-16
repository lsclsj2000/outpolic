package outpolic.user.search.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

@Mapper
public interface UserSearchHistoryMapper {

	/**
     * search_history 테이블의 다음 sh_cd를 생성합니다.
     * @return 생성할 다음 sh_cd (예: SH_C0000001)
     */
    String getNextShCd();

    /**
     * 검색 기록을 search_history 테이블에 삽입합니다.
     * @param shCd 생성된 PK
     * @param shTerm 검색어
     * @param mbrCd 사용자 코드
     */
    void insertSearchHistory(@Param("shCd") String shCd,
                             @Param("shTerm") String shTerm,
                             @Param("mbrCd") String mbrCd);
}
