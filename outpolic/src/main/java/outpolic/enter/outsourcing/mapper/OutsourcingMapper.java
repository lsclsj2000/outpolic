package outpolic.enter.outsourcing.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import outpolic.enter.outsourcing.domain.EnterOutsourcing;

/**
 * @Mapper: MyBatis에게 이 파일이 데이터베이스와 소통하는 청구임을 알려주는 어노테이션입니다.
 * 여기에 적힌 메서드 이름과 똑같은 이름의 쿼리를 XML 파일에서 찾아 실행합니다. 
 */
@Mapper
public interface OutsourcingMapper {
   
	// --- 조회 (SELECT) ---
	/**
	 * 특정 기업이 등록한 모든 외주 글 목록을 가져옵니다.
	 */
	List<EnterOutsourcing> findOutsourcingDetailsByEntCd(String entCd);
	
	/**
	 * 외주 글 하나의 상세 정보를 가져옵니다.
	 */
	EnterOutsourcing findOutsourcingDetailsByOsCd(String osCd);
	
	/**
	 * 이름으로 태그를 검색합니다. (태그 추천 기능에서 사용)
	 */
	List<String> searchTagsByName(@Param("query") String query);
	
	/**
	 * 가장 마지막에 만들어진 외주 코드(번호)를 찾습니다 (새로운 외주 코드 생성 시 참고용)
	 */
	String findLatestOsCd();
	/**
	 * 가장 마지막에 만들어진 태그 코드를 찾습니다.
	 */
	String findLatestTagCd();
	
	/**
	 * 태그 이름으로 태그 코드를 찾습니다. (DB에 이미 있는 태그인지 확인용)
	 */
	String findTagCdByName(String tagName);
	
	/**
	 * 외주 코드로 콘텐츠 코드를 찾습니다. (다름 정보들과 연결된 키를 찾을 때 사용)
	 */
	String findClCdByOsCd(String osCd);
	
	/** 
	 *  가장 마지막에 만들어진 '외주-포트폴리오 연결' 코드(번호)를 찾습니다.
	 */
	String findLatestOpCd();
	
	// --- 저장 (INSERT) ---
	/**
	 * 새로운 외주 글의 기본 정보를 저장합니다.
	 */
	int insertOutsourcing(EnterOutsourcing outsourcing);
	
	/**
	 * 외주 글을 전체 콘텐츠 목록에 추가합니다. (다른 기능과 연동하기 위함)
	 */
	int insertContentList(@Param("clCd") String clCd, @Param("cntdCd") String cntdCd);
	
	/**
	 * 외주 글과 카테고리를 연결하는 정보를 저장합니다.
	 */
	int insertCategoryMapping(@Param("ctgryCd") String ctgryCd, @Param("clCd") String clCd, @Param("mbrCd") String mbrCd);
	
	/**
	 * 새로운 태그를 만듭니다.
	 */
	int insertTag(@Param("tagCd") String tagCd, @Param("tagName") String tagName, @Param("mbrCd") String mbrCd);
	
	/**
	 * 외주 글과 태그를 연결하는 정보를 저장합니다.
	 */
	int insertTagMapping(@Param("tagCd") String tagCd, @Param("clCd") String clCd, @Param("mbrCd") String mbrCd);
	
	/**
	 * 외주 글과 포트폴리오를 연결하는 정보를 저장합니다.
	 */
	int insertOutsourcingPortfolio(@Param("opCd") String opCd, @Param("osCd") String osCd,@Param("prtfCd") String prtfCd, @Param("entCd") String entCd);
	
	// --- 수정(UPDATE) ---
	/**
	 * 기존 외주 글의 내용을 수정합니다.
	 */
	int updateOutsourcing(EnterOutsourcing outsourcing);
	
	// --- 삭제 (DELETE) ---
	
	/** 
	 * 외주 글 자체를 삭제합니다.
	 */
	int deleteOutsourcingByOsCd(String osCd);
	
	/**
	 * 외주 글에 연결된 카테고리 정보를 모두 삭제합니다.
	 */
	int deleteCategoryMappingByClCd(String clCd);
	
	/**
	 * 외주 글에 연결된 태그 정보를 모두 삭제합니다.
	 */
	int deleteTagMappingByClCd(String clCd);
	
	/**
	 * 외주 글에 연결된 포트폴리오 정보를 모두 삭제합니다. 
	 */
	int deleteOutsourcingPortfolioByOsCd(String osCd);
	
	/**
	 * 외주 글을 전체 콘텐츠 목록에서 삭제합니다.
	 */
	int deleteContentListByClCd(String clCd);
	
	/**
	 * 외주 글과 관련된 기타 부가 정보들을 모두 삭제합니다.
	 * (이하 메서드들은 외주 글이 삭제될 때 함께 정리되어야 하는 데이터들입니다.)
	 */
	int deleteBookmarkByClCd(String clCd);
    int deleteOutsourcingContractDetailsByClCd(String clCd);
    int deleteOutsourcingStatusByOcdCd(String ocdCd);
    int deleteOutsourcingStatusByClCd(String clCd);
    int deleteRankingByClCd(String clCd);
    int deleteTodayViewByClCd(String clCd);
    int deleteTotalViewByClCd(String clCd);
}
