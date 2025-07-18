package outpolic.enter.search.service;

public interface EnterBookmarkService {
	
	 /**
     * 사용자의 찜 목록에 콘텐츠를 추가합니다.
     * @param userId 현재 로그인한 사용자의 ID (mbr_cd)
     * @param clCd 찜할 콘텐츠의 ID (cl_cd)
     */
    void addBookmark(String userId, String clCd);

    /**
     * 사용자의 찜 목록에서 콘텐츠를 삭제합니다.
     * @param userId 현재 로그인한 사용자의 ID (mbr_cd)
     * @param clCd 찜 해제할 콘텐츠의 ID (cl_cd)
     */
    void deleteBookmark(String userId, String clCd);
}