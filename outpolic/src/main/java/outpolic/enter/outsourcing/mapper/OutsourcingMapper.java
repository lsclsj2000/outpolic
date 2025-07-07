package outpolic.enter.outsourcing.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import outpolic.enter.outsourcing.domain.EnterOutsourcing;

@Mapper
public interface OutsourcingMapper {
    int insertOutsourcing(EnterOutsourcing outsourcing);
    int insertContentList(@Param("clCd") String clCd, @Param("cntdCd") String cntdCd);
    int insertCategoryMapping(@Param("ctgryCd") String ctgryCd, @Param("clCd") String clCd, @Param("mbrCd") String mbrCd);
    int insertTag(@Param("tagCd") String tagCd, @Param("tagName") String tagName, @Param("mbrCd") String mbrCd);
    int insertTagMapping(@Param("tagCd") String tagCd, @Param("clCd") String clCd, @Param("mbrCd") String mbrCd);
    
    List<EnterOutsourcing> findOutsourcingDetailsByEntCd(String entCd);
    List<String> searchTagsByName(@Param("query") String query);
    
    String findLatestOsCd();
    String findLatestTagCd();
    String findTagCdByName(String tagName);
    
    EnterOutsourcing findOutsourcingDetailsByOsCd(String osCd);
    String findClCdByOsCd(String osCd);
    int deleteCategoryMappingByClCd(String clCd);
    int deleteTagMappingByClCd(String clCd);
    int updateOutsourcing(EnterOutsourcing outsourcing);
    
    int deleteContentListByClCd(String clCd);
    int deleteOutsourcingByOsCd(String osCd);
    int deleteBookmarkByClCd(String clCd); // 추가
    int deleteOutsourcingContractDetailsByClCd(String clCd); // 추가
    int deleteOutsourcingStatusByOcdCd(String ocdCd);
    int deleteOutsourcingStatusByClCd(String clCd); // <-- 이 줄을 추가합니다.
    int deleteRankingByClCd(String clCd);
    int deleteTodayViewByClCd(String clCd); 
    int deleteTotalViewByClCd(String clCd);
    
    // outsourcing_portfolio 테이블 관련
    String findLatestOpCd();
    int insertOutsourcingPortfolio(@Param("opCd") String opCd, @Param("osCd") String osCd,@Param("prtfCd") String prtfCd, @Param("entCd") String entCd);
    int deleteOutsourcingPortfolioByOsCd(String osCd);

}
