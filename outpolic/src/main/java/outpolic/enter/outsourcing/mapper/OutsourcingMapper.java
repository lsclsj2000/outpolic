package outpolic.enter.outsourcing.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import outpolic.enter.outsourcing.domain.EnterOutsourcing;
import outpolic.enter.portfolio.domain.EnterPortfolio; // EnterPortfolio import 유지

@Mapper
public interface OutsourcingMapper {
    // --- 조회 (SELECT) ---
    List<EnterOutsourcing> findOutsourcingDetailsByEntCd(String entCd);
    EnterOutsourcing findOutsourcingDetailsByOsCd(String osCd);
    List<String> searchTagsByName(@Param("query") String query);
    String findLatestOsCd();
    String findLatestTagCd();
    String findTagCdByName(String tagName);
    String findClCdByOsCd(String osCd);
    String findLatestOpCd(); // outsourcing_portfolio 테이블의 PK 조회

    // --- 저장 (INSERT) ---
    int insertOutsourcing(EnterOutsourcing outsourcing);
    int insertContentList(@Param("clCd") String clCd, @Param("cntdCd") String cntdCd);
    int insertCategoryMapping(@Param("ctgryCd") String ctgryCd, @Param("clCd") String clCd, @Param("mbrCd") String mbrCd);
    int insertTag(@Param("tagCd") String tagCd, @Param("tagName") String tagName, @Param("mbrCd") String mbrCd);
    int insertTagMapping(@Param("tagCd") String tagCd, @Param("clCd") String clCd, @Param("mbrCd") String mbrCd);
    int insertOutsourcingPortfolio(@Param("opCd") String opCd, @Param("osCd") String osCd, @Param("prtfCd") String prtfCd, @Param("entCd") String entCd);

    // --- 수정(UPDATE) ---
    int updateOutsourcing(EnterOutsourcing outsourcing);

    // --- 삭제 (DELETE) ---
    int deleteOutsourcingByOsCd(String osCd);
    int deleteCategoryMappingByClCd(String clCd);
    int deleteTagMappingByClCd(String clCd);
    int deleteOutsourcingPortfolioByOsCd(@Param("osCd") String osCd); // osCd 기반 삭제
    int deletePortfolioLinkByPrtfCd(@Param("prtfCd") String prtfCd); // Portfolio 삭제 시 연결 삭제용으로 추가
    int deleteContentListByClCd(String clCd);
    int deleteBookmarkByClCd(String clCd);
    int deleteOutsourcingContractDetailsByClCd(String clCd);
    int deleteOutsourcingStatusByOcdCd(String ocdCd);
    int deleteOutsourcingStatusByClCd(String clCd);
    int deleteRankingByClCd(String clCd);
    int deleteTodayViewByClCd(String clCd);
    int deleteTotalViewByClCd(String clCd);

    // --- 외주-포트폴리오 연결을 위한 매퍼 메서드 (추가/유지) ---
    // 특정 외주에 연결된 포트폴리오 목록 조회
    List<EnterPortfolio> findLinkedPortfoliosByOsCd(@Param("osCd") String osCd);
    // List<EnterPortfolio> findUnlinkedPortfolios(@Param("osCd") String osCd, @Param("entCd") String entCd, @Param("query") String query); // PortfolioMapper로 이동

    // 외주에서 포트폴리오 연결 해제
    int unlinkOutsourcingFromPortfolio(@Param("osCd") String osCd, @Param("prtfCd") String prtfCd);
}