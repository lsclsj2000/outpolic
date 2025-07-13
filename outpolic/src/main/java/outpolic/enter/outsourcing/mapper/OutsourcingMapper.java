package outpolic.enter.outsourcing.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import outpolic.enter.outsourcing.domain.EnterOutsourcing;
import outpolic.enter.portfolio.domain.EnterPortfolio;

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
    String findLatestOpCd();

    // ★ 추가: 모든 외주 목록을 가져오는 메서드 (findAllOutsourcings 오류 해결)
    List<EnterOutsourcing> findAllOutsourcings();

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
    int deleteContentListByClCd(String clCd);
    int deleteCategoryMappingByClCd(String clCd);
    int deleteTagMappingByClCd(String clCd);
    int deleteOutsourcingPortfolioByOsCd(@Param("osCd") String osCd);
    int deleteBookmarkByClCd(String clCd);
    int deleteOutsourcingContractDetailsByClCd(String clCd);
    int deleteOutsourcingStatusByOcdCd(String ocdCd);
    int deleteOutsourcingStatusByClCd(String clCd);
    int deleteRankingByClCd(String clCd);
    int deleteTodayViewByClCd(String clCd);
    int deleteTotalViewByClCd(String clCd);
    int deletePortfolioLinkByPrtfCd(@Param("prtfCd") String prtfCd);
    void updateOutsourcingStep1(EnterOutsourcing outsourcing);

    // --- 외주-포트폴리오 연결을 위한 매퍼 메서드 ---
    List<EnterPortfolio> findLinkedPortfoliosByOsCd(@Param("osCd") String osCd);
    int unlinkOutsourcingFromPortfolio(@Param("osCd") String osCd, @Param("prtfCd") String prtfCd);
    void updateOutsourcingRepresentativeCategory(@Param("osCd") String osCd, @Param("ctgryId") String ctgryId);
}