package outpolic.enter.portfolio.mapper;


import outpolic.enter.POAddtional.domain.CategorySearchDto;
import outpolic.enter.outsourcing.domain.EnterOutsourcing;
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
    
    List<String> searchTagsByName(@Param("query") String query);
    
    /**
     * 특정 포트폴리오에 이미 연결된 외주 목록을 조회하는 메서드
     */
    List<EnterOutsourcing> findLinkedOutsourcingsByPrtfCd(String prtfCd);
    
    /**
     * 특정 포트폴리오에 아직 연결되지 않은 외주들을 검색하는 메서드
     */
    List<EnterOutsourcing> findUnlinkedOutsourcings(@Param("prtfCd") String prtfCd, @Param("entCd") String entCd,@Param("query") String query);
    
    /**
     * 포트폴리오와 외주를 연결하는 메서드(INSERT)
     */
    int linkOutsourcingToPortfolio(@Param("opCd") String opCd, @Param("osCd") String osCd,@Param("prtfCd") String prtfCd,@Param("entCd") String entCd);
    
    /**
     * 포트폴리오와 외주의 연결을 해체하는 메서드(DELETE)
     */
    int unlinkOutsourcingFromPortfolio(@Param("osCd") String osCd, @Param("prtfCd") String prtfCd);
    
    
    
    
    
    
}