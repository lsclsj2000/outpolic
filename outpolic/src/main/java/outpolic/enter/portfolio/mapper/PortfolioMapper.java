package outpolic.enter.portfolio.mapper;


import outpolic.enter.POAddtional.domain.CategorySearchDto;
import outpolic.enter.portfolio.domain.EnterPortfolio;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;



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
    

    // UPDATE
    int updatePortfolio(EnterPortfolio portfolio);
    
    
    int countPortfoliosByEntCd(String entCd);
}