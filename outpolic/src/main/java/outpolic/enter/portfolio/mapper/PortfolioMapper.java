package outpolic.enter.portfolio.mapper;

import outpolic.enter.POAddtional.domain.CategorySearchDto;
import outpolic.enter.outsourcing.domain.EnterOutsourcing;
import outpolic.enter.portfolio.domain.EnterPortfolio;
import outpolic.systems.file.domain.FileMetaData;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


@Mapper
public interface PortfolioMapper {

    List<EnterPortfolio> findPortfoliosByTitle(@Param("query") String query);

    // INSERT
    int insertPortfolio(EnterPortfolio portfolio);
    int insertContentList(@Param("clCd") String clCd, @Param("cntdCd") String cntdCd);
    int insertCategoryMapping(@Param("ctgryCd") String ctgryCd, @Param("clCd") String clCd, @Param("mbrCd") String mbrCd);
    int insertTag(@Param("tagCd") String tagCd, @Param("tagName") String tagName, @Param("mbrCd") String mbrCd);
    int insertTagMapping(@Param("tagCd") String tagCd, @Param("clCd") String clCd, @Param("mbrCd") String mbrCd);

    // SELECT (Helper)
    String findLatestPrtfCd();
    String findLatestTagCd();
    String findTagCdByName(String tagName);
    String findClCdByPrtfCd(String prtfCd);

    // SELECT (Main)
    List<EnterPortfolio> findPortfolioDetailsByEntCd(String entCd);
    EnterPortfolio findPortfolioDetailsByPrtfCd(String prtfCd);
    // SELECT (for ResultMap Collections)
    List<CategorySearchDto> findCategoriesByPrtfCd(String prtfCd);
    List<String> findTagNamesByPrtfCd(String prtfCd);

    // DELETE
    int deletePortfolioByPrtfCd(String prtfCd);
    int deleteContentListByClCd(String clCd);
    int deleteCategoryMappingByClCd(String clCd);
    int deleteTagMappingByClCd(String clCd);
    int deleteBookmarkByClCd(String clCd);
    int deleteFilesByClCd(String clCd);

    // UPDATE
    int updatePortfolio(EnterPortfolio portfolio);

    int countPortfoliosByEntCd(String entCd);

    List<String> searchTagsByName(@Param("query") String query);

    // Portfolio에 연결된 Outsourcing 조회
    List<EnterOutsourcing> findLinkedOutsourcingsByPrtfCd(@Param("prtfCd") String prtfCd);
    // Portfolio에 연결되지 않은 Outsourcing 검색
    List<EnterOutsourcing> searchUnlinkedOutsourcings(@Param("prtfCd") String prtfCd, @Param("entCd") String entCd, @Param("query") String query);

    // Outsourcing에 연결되지 않은 Portfolio 검색
    List<EnterPortfolio> findUnlinkedPortfolios(@Param("osCd") String osCd, @Param("entCd") String entCd, @Param("query") String query);

    int deleteOutsourcingPortfolioByPrtfCd(@Param("prtfCd") String prtfCd);

    @Select("SELECT ent_cd FROM enterprise WHERE mbr_cd = #{mbrCd}")
    String findEntCdByMbrCd(String mbrCd);
    String findMbrCdByClCd(String clCd);

    void insertFileRecord(@Param("file") FileMetaData fileData, @Param("clCd") String clCd, @Param("mbrCd") String mbrCd);
    List<FileMetaData> findFilesByClCd(String clCd);

    void addPortfolio(EnterPortfolio portfolio);
    List<EnterPortfolio> searchPortfoliosByTitle(String query);

    // Missing method: Outsourcing에 연결된 Portfolio 조회
    // 이 메서드는 PortfolioMapper에 있어야 합니다.
    List<EnterPortfolio> findLinkedPortfoliosByOsCd(String osCd); // <-- 이 줄을 추가합니다.

    int deleteOutsourcingContractDetailsByClCd(String clCd);
    int deleteRankingByClCd(String clCd);
    int deleteTodayViewByClCd(String clCd);
    int deleteTotalViewByClCd(String clCd);
    int deletePerusalContentByClCd(String clCd); // <-- 이 줄을 추가합니다.

    String findLatestClCdForPortfolio();

    String findMbrCdByPrtfCd(String prtfCd);

	List<EnterOutsourcing> findLinkedOutsourcingsByOsCd(String osCd);
}