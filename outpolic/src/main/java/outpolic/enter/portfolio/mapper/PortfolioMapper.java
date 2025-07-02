package outpolic.enter.portfolio.mapper;

import outpolic.enter.portfolio.domain.CategorySearchDto;
import outpolic.enter.portfolio.domain.EnterPortfolio;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface PortfolioMapper {
    int insertPortfolio(EnterPortfolio portfolio);
    int insertContentList(@Param("clCd") String clCd, @Param("cntdCd") String cntdCd);
    int insertCategoryMapping(@Param("ctgryCd") String ctgryCd, @Param("clCd") String clCd, @Param("mbrCd") String mbrCd);
    int insertTag(@Param("tagCd") String tagCd, @Param("tagName") String tagName, @Param("mbrCd") String mbrCd);
    int insertTagMapping(@Param("tagCd") String tagCd, @Param("clCd") String clCd, @Param("mbrCd") String mbrCd);
    
    String findLatestPrtfCd();
    String findLatestTagCd();
    String findTagCdByName(String tagName);
    String findClCdByPrtfCd(String prtfCd);
    
    List<EnterPortfolio> findPortfolioDetailsByEntCd(String entCd);
    EnterPortfolio findPortfolioDetailsByPrtfCd(String prtfCd);
    List<CategorySearchDto> findCategoriesByPrtfCd(String prtfCd);
    List<String> findTagNamesByPrtfCd(String prtfCd);
    
    int deletePortfolioByPrtfCd(String prtfCd);
    int deleteContentListByClCd(String clCd);
    int deleteCategoryMappingByClCd(String clCd);
    int deleteTagMappingByClCd(String clCd);
    void deleteFileByClCd(String clCd);     
    void deleteBookmarkByClCd(String clCd);    
    int updatePortfolio(EnterPortfolio portfolio);
    void deleteOutsourcingContractDetailsByClCd(String clCd);
    void deleteTotalViewByClCd(String clCd);
    void deleteOutsourcingStatusByClCd(String clCd);
    void deleteRankingByClCd(String clCd);
    void deleteOutsourcingPortfolioByPrtfCd(String prtfCd);

}